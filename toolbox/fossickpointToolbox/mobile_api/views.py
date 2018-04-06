# -*- coding: utf-8 -*-
from __future__ import unicode_literals

import ast
import json
from django.http import JsonResponse, HttpResponse
from django.shortcuts import render

# Create your views here.
from pip._vendor import requests

from mobile_api import constants
from toolbox.models import User, PersonalInfo


def register(request):
    if (request.method=="POST"):
        print("POST REGISTER")
        body = json.loads(request.body.decode('utf-8'))
        content = body
        print(content)
        userName = body["userName"]
        password = body["password"]
        gender = body["gender"]
        email = body["email"]
        userNameValid = False
        emailValid = False
        # Check username
        try:
            checkUserName = User.objects.get(userName = userName)
        except:
            userNameValid = True
        # Check email
        try:
            checkEmail = PersonalInfo.objects.get(email = email)
        except:
            emailValid = True
        if(userNameValid and emailValid):
            user = User(userName=userName, userPassword=password, userType=1)
            user.save()
            if (gender == "male"):
                personalInfo = PersonalInfo(user=user, gender=0, name=userName, email=email)
            else:
                personalInfo = PersonalInfo(user=user, gender=1, name=userName, email=email)
            personalInfo.save()
            return JsonResponse({"status": 1, "name":userName})
        else:
            dict = {}
            if(not userNameValid):
                dict.update({"userName" : -1})
            if(not emailValid):
                dict.update({"email": -1})
            return JsonResponse(dict)

# /login
def login(request):
    print("POST TYPE: "+request.method)
    if (request.method=="POST"):
        print("POST")
        """userName = request.POST.get('userName')
        password = request.POST.get('password')"""
        body = json.loads(request.body.decode('utf-8'))
        content = body
        userName = body['userName']
        password = body['password']
        print(content)
        user_exist = False
        password_correct = False
        try:
            user = User.objects.get(userName = userName)
            user_exist = True
            if (user.userPassword == password):
                # request.session['uuid'] = user.id
                password_correct = True
            else:
                password_correct = False
        except:
            user_exist = False

        if(user_exist and password_correct):
            return JsonResponse({"status": 1, "desc": "login successful"})
        else:
            return JsonResponse({"status": -1, "desc": "login failed"})

def redeem_code(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        userName = body['userName']
        coupon_code = body["coupon_code"]
        user_model = User.objects.get(userName = userName)
        personal_info = PersonalInfo.objects.get(user = user_model)
        coupon_available = False
        try:
            personal_info = PersonalInfo.objects.get(coupon_code=coupon_code)
        except:
            coupon_available = True

        if(coupon_available):
            """
            Redeem code from acuity, since the API requires to determine the type of coupon and the mobile front end
            required not to determine the type of coupon, so the API will check the coupon and each type one by one
            """
            r1 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_1), headers=constants.HEADERS)
            r2 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_2), headers=constants.HEADERS)
            r3 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_3), headers=constants.HEADERS)
            print("r1_response: "+r1.text)
            print("r2_response: " + r2.text)
            print("r3_response: " + r3.text)
            r1_response = json.loads(r1.text) #ast.literal_eval(r1.text)
            r2_response = json.loads(r2.text)
            r3_response = json.loads(r3.text)
            list = [r1_response, r2_response, r3_response]
            appointmentTypeId = None
            plan_name = None
            for i in list:
                if (not "status_code" in i):
                    appointmentTypeId = i["appointmentTypeIDs"][0]
                    plan_name = i["name"]
            user_type = None
            if (appointmentTypeId == constants.PLAN_1):
                user_type = 2
            elif (appointmentTypeId == constants.PLAN_2):
                user_type = 3
            elif (appointmentTypeId == constants.PLAN_1):
                user_type = 4

            if (appointmentTypeId != None):
                personal_info.coupon_code = coupon_code
                personal_info.save()

                if (user_type != None):
                    user_model.userType = user_type
                    user_model.save()
                    return JsonResponse({"status":1, "user": user_model.userName, "userType":user_model.userType,
                                         "coupon_code": personal_info.coupon_code, "plan_name":plan_name,
                                         "appointment_type_id":appointmentTypeId})
                else:
                    return JsonResponse({"status": -1, "desc": "PLAN NOT ASSIGNED FROM THE BACKEND YET"})
            else:
                return JsonResponse({"status": -1, "desc": "coupon code is not available"})
        else:
            return JsonResponse({"status":-1, "desc":"coupon code is not available"})

def profile_detail(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        userName = body['userName']
        try:
            user_model = User.objects.get(userName=userName)
            personal_info = PersonalInfo.objects.get(user=user_model)
            coupon_code = personal_info.coupon_code

            r1 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_1), headers=constants.HEADERS)
            r2 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_2), headers=constants.HEADERS)
            r3 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                              format(coupon_code, constants.PLAN_3), headers=constants.HEADERS)
            r1_response = json.loads(r1.text)  # ast.literal_eval(r1.text)
            r2_response = json.loads(r2.text)
            r3_response = json.loads(r3.text)
            list = [r1_response, r2_response, r3_response]
            appointmentTypeId = None
            plan_name = None
            for i in list:
                if (not "status_code" in i):
                    appointmentTypeId = i["appointmentTypeIDs"][0]
                    plan_name = i["name"]
                elif (i["status_code"]==400):
                    error = i["error"]
                    if(error=="expired_certificate"):
                        personal_info.coupon_code = None
                        user_model.userType = 1
                        personal_info.save()
                        user_model.save()

            print(personal_info)
            return JsonResponse({"status":1, "username": user_model.userName, "user_type": user_model.userType,
                                 "email":personal_info.email, "coupon_code":personal_info.coupon_code,
                                 "appointment_type_id":appointmentTypeId, "plan_name":plan_name})
        except:
            return JsonResponse({"status": -1})

def get_available_schedule(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        appointment_type_id = body["appointment_type_id"]
        date = body["date"] # YYYY-mm-dd
        r = requests.get(constants.ACUITY_CHECK_AVAILABLE_TIME + "?appointmentTypeID={}&date={}".
                          format(appointment_type_id, date), headers=constants.HEADERS)
        print("get_available_schedule: "+constants.ACUITY_CHECK_AVAILABLE_TIME + "?appointmentTypeID={}&date={}".
                          format(appointment_type_id, date))
        r_response = json.loads(r.text)
        if("status_code" in r_response):
            return JsonResponse({"status": -1})
        else:
            return(JsonResponse({"status": 1, "available_dates":r_response}))

def post_schedule(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        datetime = body["datetime"] # yyyy-MM-dd'T'hh:mm:ss[+|-]hh:mm
        userName = body["userName"]
        appointment_type_id = body["appointment_type_id"]
        user_model = User.objects.get(userName=userName)
        personal_info = PersonalInfo.objects.get(user=user_model)
        email = personal_info.email
        certificate = personal_info.coupon_code

        dict = {"datetime":datetime, "appointmentTypeID":appointment_type_id, "firstName":userName, "lastName":userName,
                "email":email, "certificate":certificate}
        r = requests.post(constants.ACUITY_POST_SCHEDULE, headers=constants.HEADERS, json=dict)
        r_response = json.loads(r.text)
        if("status_code" in r_response):
            return JsonResponse({"status": -1})
        else:
            return JsonResponse({"status": 1})

def get_schedule_for_user(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        userName = body["userName"]
        r = requests.get(constants.ACUITY_POST_SCHEDULE + "?firstName={}".
                          format(userName), headers=constants.HEADERS)
        r_response = json.loads(r.text)
        if("status_code" in r_response):
            return JsonResponse({"status": -1})
        else:
            list = []
            for i in r_response:
                items = {}
                items["id"] = i["id"]
                items["time"] = i["time"]
                items["endTime"] = i["endTime"]
                items["datetime"] = i["datetime"]
                items["type"] = i["type"]
                list.append(items)
                items = {}
            return JsonResponse({"status":1, "available_dates":list})

def cancel_schedule(request):
    if (request.method == "POST"):
        body = json.loads(request.body.decode('utf-8'))
        appointment_id = body["appointment_id"]
        r = requests.put(constants.ACUITY_POST_SCHEDULE + "/{}/cancel".
                         format(appointment_id), headers=constants.HEADERS)
        r_response = json.loads(r.text)
        if("status_code" in r_response):
            return JsonResponse({"status": -1})
        else:
            return JsonResponse({"status": 1, "desc": "schedule canceled successfully"})