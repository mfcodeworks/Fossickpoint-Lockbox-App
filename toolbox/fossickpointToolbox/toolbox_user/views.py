import json
import datetime
from pip._vendor import requests
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.shortcuts import render
from toolbox.models import Content, User, PersonalInfo,ContentCategory, Program, ProgramDetail, Plan
import mobile_api.constants as constants
import math
from django.db.models import Q

# Create your views here.
def index(request):
    if 'uuid' in request.session:
        return HttpResponseRedirect('home')
    else:
        return render(request, "toolbox_user/login.html")

# /login
def login(request):
    if (request.POST):
        email = request.POST.get('email')
        password = request.POST.get('password')
        try:
            personalInfo = PersonalInfo.objects.get(email=email)
            user = personalInfo.user
        except:
            return HttpResponse("login failed")

        if (user is not None and user.userPassword == password):
            request.session['uuid'] = user.id
            return JsonResponse({"status": 1})
        else:
            return JsonResponse({"status": -1})

# /logout
def logout(request):
    del request.session['uuid']
    return HttpResponseRedirect('/toolbox_user/')

def register(request):
    if (request.POST):
        userName = request.POST.get('userName')
        password = request.POST.get('password')
        gender = request.POST.get('gender')
        email = request.POST.get('email')
        try:
            personalInfo = PersonalInfo.objects.get(email=email)
            return JsonResponse({"status":-1})
        except:
            user = User(userName=userName, userPassword=password, userType=1)

            user.save()
            if (gender == "male"):
                personalInfo = PersonalInfo(user=user,gender=0,name=userName,email=email)
            else:
                personalInfo = PersonalInfo(user=user,gender=1,name=userName,email=email)
            personalInfo.save()
            return JsonResponse({"status": 1})
    return render(request,"toolbox_user/register.html")

def plan(request):
    if 'uuid' in request.session:
        user = User.objects.get(id=request.session["uuid"])
        if (request.POST):
            certificate_code = request.POST.get('certificate_code')
            status, desc = verifyCode(certificate_code, user)
            return JsonResponse({"status": status, "desc": desc})
        plan_list_model = Plan.objects.all()
        planList = [dict(plan) for plan in plan_list_model]
        context = {"user": user, "planList": planList, 'rootCategories': getRootCategories()}
        return render (request, 'toolbox_user/plan.html', context)
    else:
        return HttpResponseRedirect('/toolbox_user/')

def verifyCode(certificate_code, user_model):
    import mobile_api.constants as constants
    print(certificate_code)
    personal_info = PersonalInfo.objects.get(user=user_model)
    code_available = False
    try:
        personal_info = PersonalInfo.objects.get(toolbox_certificate_code=certificate_code)
    except:
        code_available = True

    if (code_available):
        """
        Redeem code from acuity, since the API requires to determine the type of coupon and the mobile front end
        required not to determine the type of coupon, so the API will check the coupon and each type one by one
        """
        unlimited_plan = Plan.objects.get(plan_id=2).plan_acuity_appointment_id
        indivisualized_plan = Plan.objects.get(plan_id=3).plan_acuity_appointment_id
        premium_plan = Plan.objects.get(plan_id=4).plan_acuity_appointment_id        
        r1 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                          format(certificate_code, unlimited_plan), headers=constants.HEADERS)
        r2 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                          format(certificate_code, indivisualized_plan), headers=constants.HEADERS)
        r3 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                          format(certificate_code, premium_plan), headers=constants.HEADERS)
        print("r1_response: " + r1.text)
        print("r2_response: " + r2.text)
        print("r3_response: " + r3.text)
        r1_response = json.loads(r1.text)  # ast.literal_eval(r1.text)
        r2_response = json.loads(r2.text)
        r3_response = json.loads(r3.text)
        list = [r1_response, r2_response, r3_response]
        appointmentTypeId = None
        plan_name = None
        for i in list:
            if (not "status_code" in i or i["error"] != "invalid_certificate_type"):
                appointmentTypeId = i["appointmentTypeIDs"][0]
                plan_name = i["name"]
        toolbox_user_type = None
        if (appointmentTypeId == unlimited_plan):
            toolbox_user_type = 2
        if (appointmentTypeId == unlimited_plan):
            toolbox_user_type = 3
        if (appointmentTypeId == unlimited_plan):
            toolbox_user_type = 4      

        if (toolbox_user_type):
            user_model.toolbox_user_type = toolbox_user_type
            user_model.save()
            personal_info.toolbox_certificate_code = certificate_code
            personal_info.save()
            return 1, "Update user plan successfully"
        else:
            return -1, "The code is invalid"
    else:
        return -1, "The code has been used."

def home(request):
    if 'uuid' in request.session:
        user= User.objects.get(id=request.session["uuid"])
        categories = ContentCategory.objects.filter(parent = None)
        childrenDict = []
        for category in categories:
            if category:
                childrenDict.append(dict(category))
        # context = getContentList(user.userType)
        context = {'user': user, 'siblings': categories, 'children': childrenDict, 'childrenType': 'category', 'currentCategory': categories[0].name, 'rootCategories': getRootCategories()}
        return render(request, 'toolbox_user/index.html', context)
    else:
        return HttpResponseRedirect('/toolbox_user/')

def contents(request, category):
    if 'uuid' in request.session:
        user= User.objects.get(id=request.session["uuid"])
        currentCategory = ContentCategory.objects.get(name = category)
        siblings = ContentCategory.objects.filter(parent = currentCategory.parent)
        children = ContentCategory.objects.filter(parent = currentCategory)
        childrenDict = []
        if children:
            for child in children:
                childrenDict.append(dict(child))
            context = {'siblings': siblings, 'children': childrenDict, 'childrenType': 'category','currentCategory': category, 'rootCategories': getRootCategories()}
            return render(request, 'toolbox_user/index.html', context)
        else:
            children = Content.objects.filter(category=currentCategory)
            if user.userType == 1:
                children = children.filter(level=1)
            else:
                children = children.filter(level__lte=2)
            for child in children:
                childrenDict.append(dict(child))
            context = {'siblings': siblings, 'children': childrenDict, 'childrenType': 'content','currentCategory': category, 'rootCategories': getRootCategories()}
        
        return render(request, 'toolbox_user/contents.html', context)
        
    else:
        return HttpResponseRedirect('/toolbox_user/')


# content detail
def content(request, contentID):
    # todo filter high level content that can't be access directly.
    if 'uuid' in request.session:
        targetContent = Content.objects.get(id=contentID)
        targetContent = dict(targetContent)
        context = {"content": targetContent, 'rootCategories': getRootCategories()}
        return render(request, "toolbox_user/content.html", context)
    else:
        return HttpResponseRedirect('/toolbox_user/')

# program list page
def programlist(request):
    if 'uuid' in request.session:
        user= User.objects.get(id=request.session["uuid"])
        personal = PersonalInfo.objects.get(user=user)
        programs = personal.programs.all()
        context = {"programs": programs, 'rootCategories': getRootCategories()}
        return render(request, "toolbox_user/program_list.html", context)
    else:
        return HttpResponseRedirect('/toolbox_user/')   

#program page
def program(request, programID):
    if 'uuid' in request.session:
        program = Program.objects.get(id=programID)
        programDetails = ProgramDetail.objects.filter(program=program)
        programContents = []
        for programDetail in programDetails:
            programContents.append(dict(programDetail.content))
        context = {"program": program, "programContents": programContents, 'rootCategories': getRootCategories()}
        return render(request, "toolbox_user/program.html", context)
    else:
        return HttpResponseRedirect('/toolbox_user/')    

def getRootCategories():
    rootCategories = ContentCategory.objects.filter(parent=None)
    return rootCategories
"""
    profile page
"""
def profile(request):
    if 'uuid' in request.session:
        user= User.objects.get(id=request.session["uuid"])
        profile = PersonalInfo.objects.get(user=user)
        if (request.POST):
            user_name = request.POST["user_name"]
            user_age = request.POST["user_age"]
            user_nation = request.POST["user_nation"]
            user_city = request.POST["user_city"]
            user_occupation = request.POST["user_occupation"]
            user_address = request.POST["user_address"]
            user_gender = 0
            if request.POST["user_gender"] == "Female":
                user_gender = 1
            profile.name = user_name
            profile.age = user_age
            profile.nation = user_nation
            profile.city = user_city
            profile.occupation = user_occupation
            profile.address = user_address
            profile.gender = user_gender
            profile.save()

            return JsonResponse({"status": 1})
        context = {"user": profile, 'rootCategories': getRootCategories()}
        return render(request, "toolbox_user/profile.html", context)       
    else:
        return HttpResponseRedirect('/toolbox_user/')
"""
    session page
"""
def session(request):
    import mobile_api.constants as constants
    if 'uuid' in request.session:
        user= User.objects.get(id=request.session["uuid"])
        personalInfo = PersonalInfo.objects.get(user=user)
        userPlan = Plan.objects.get(plan_id=user.toolbox_user_type)
        plan_id = userPlan.plan_acuity_appointment_id
        if (request.POST):
            # cancel appointment
            if (request.POST.get('operation') == 'cancel'):
                appointment_id = request.POST.get('appointment_id')
                r = requests.put(constants.ACUITY_POST_SCHEDULE + "/{}/cancel".
                                 format(appointment_id), headers=constants.HEADERS)
                r_response = json.loads(r.text)
                if("status_code" in r_response):
                    return JsonResponse({"status": -1, "desc": "Schedule canceled failed"})
                else:
                    return JsonResponse({"status": 1, "desc": "Schedule canceled successfully"})
            elif (request.POST.get('operation') == 'book'):
                dict = {"datetime":request.POST.get("datetime"),
                        "appointmentTypeID":plan_id,
                        "firstName":user.userName,
                        "lastName":user.userName,
                        "email":personalInfo.email,
                        "certificate":personalInfo.toolbox_certificate_code}
                r = requests.post(constants.ACUITY_POST_SCHEDULE, headers=constants.HEADERS, json=dict)
                r_response = json.loads(r.text)
                if (r_response["status_code"] == "400"):
                    return JsonResponse({"status": -1, "desc": "You can only book two sessions"})
                else:
                    return JsonResponse({"status": 1, "desc": "Book the schedule successfully."})
        if (user.toolbox_user_type < 3):
            context = {"error_message": "You have no access to this page"}
            return render(request, "toolbox_user/error.html", context)      
        else:
            r1 = requests.get(constants.ACUITY_CERTIFICATES_CHECK + "?certificate={}&appointmentTypeID={}".
                          format(personalInfo.toolbox_certificate_code, plan_id), headers=constants.HEADERS)
            r_response = json.loads(r1.text)
            print(r_response)
            if ("error" in r_response and r_response["error"] == "expired_certificate"):
                user.toolbox_user_type = 1
                personalInfo.toolbox_certificate_code = ""
                user.save()
                personalInfo.save()
                context = {"error_message": "Your plan has expired. Please activate a new certificate code in plan page."}               
                return render(request, "toolbox_user/error.html", context)      
            else:
                booked_sessions = getBookedSessions(personalInfo)
                if request.GET.get('monday'):
                    monday = request.GET.get('monday')
                    try:
                        monday = datetime.datetime.strptime(monday,'%Y-%m-%d')
                        if (monday.weekday() != 0):
                            return JsonResponse({"status": -1, "desc": "wrong date"})
                    except:
                        return JsonResponse({"status": -1, "desc": "wrong date"})
                else:
                    today = datetime.date.today()
                    weekday =today.weekday()
                    monday_delta = datetime.timedelta(weekday)
                    monday = today - monday_delta
                this_week_days, available_times = getAvailableSchedule(monday, plan_id)
                context = {'booked_sessions': booked_sessions, "booking_day": this_week_days, "booking_time": available_times, 'rootCategories': getRootCategories()}
                return render(request, "toolbox_user/session.html", context)
    else:
        return HttpResponseRedirect('/toolbox_user/')


def getAvailableSchedule(monday, plan_id):
    available_times = []
    this_week_days = []
    for i in range(5):
        schedule_date = (monday + datetime.timedelta(i)).strftime("%Y-%m-%d")
        this_week_days.append(schedule_date)
        r = requests.get(constants.ACUITY_CHECK_AVAILABLE_TIME + "?appointmentTypeID={}&date={}".
                         format(plan_id, schedule_date), headers=constants.HEADERS)
        r_response = json.loads(r.text)
        today_times = []
        for r in r_response:
            # 'time': u'2018-01-22T09:00:00+1100'
            today_times.append({"day":r['time'][:10],"time":r['time'][11:19]})
        available_times.append(today_times)
    return this_week_days, available_times

def getBookedSessions(personalInfo):
    booked_sessions = []
    r = requests.get(constants.ACUITY_POST_SCHEDULE + "?email={}".
                     format(personalInfo.email), headers=constants.HEADERS)
    r_response = json.loads(r.text)
    now = datetime.datetime.now()
    for record in r_response:
        appointment_day = record['datetime'][:10]
        start_time = record['time']
        end_time = record['endTime']
        book_session_time =  datetime.datetime(int(record['datetime'][:4]), int(record['datetime'][5:7]), int(record['datetime'][8:10]), int(start_time[0:2]), int(start_time[3:5]))
        if (book_session_time > now):
            appointment_time = appointment_day + ' ' + start_time + '-' + end_time
            booked_sessions.append({'appointment_time': appointment_time, 'appointment_id': record['id']})
    return booked_sessions

def search(request):
    if 'uuid' in request.session:
        keyword = request.GET.get("keyword")
        user_model= User.objects.get(id=request.session["uuid"])
        user_type = user_model.toolbox_user_type
        search_contents_query = None
        if user_type == 1:
            search_contents_query = Content.objects.filter(
                Q(level=1) & (Q(name__contains=keyword) | Q(keyword__contains=keyword) | Q(category__name__contains=keyword))
            )
        else:
            search_contents_query = Content.objects.filter(
                Q(level__lte=2) & (Q(name__contains=keyword) | Q(keyword__contains=keyword) | Q(category__name__contains=keyword))
            )
        search_contents = []
        for content in search_contents_query:
            search_contents.append(dict(content))
        context = {'rootCategories': getRootCategories(), "searchContents": search_contents}  
        return render(request, "toolbox_user/search.html", context)         
    else:
        return HttpResponseRedirect('/toolbox_user/')