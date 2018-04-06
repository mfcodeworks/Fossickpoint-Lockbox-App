import json

from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse
from django.shortcuts import render

# Create your views here.
from django.views.decorators.csrf import csrf_exempt
from rest_framework.parsers import JSONParser

from api.models import Account
from api.serializers import AccountSerializer


@csrf_exempt
def account_list(request):
    """
    List all code snippets, or create a new snippet.
    """
    if request.method == 'GET':
        accounts = Account.objects.all()
        serializer = AccountSerializer(accounts, many=True)
        data = {'responseCode': 0,
                'responseDesc': 'Success',
                'accounts': serializer.data}
        return JsonResponse(data, safe=False)

    elif request.method == 'POST':
        data = JSONParser().parse(request)
        print("JSON BODY: "+str(data))
        serializer = AccountSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            data = {'responseCode':0,
                    'responseDesc':'Success'}
            return JsonResponse(data, safe=False)
        return JsonResponse(serializer.errors, status=400)

@csrf_exempt
def account_register(request):
    if request.method == 'POST':
        body = JSONParser().parse(request)
        username = body['username']
        password = body['password']
        username_available = False
        password_available = False
        try:
            AccountSerializer(Account.objects.get(username__exact=username))
        except ObjectDoesNotExist:
            username_available = True
        try:
            AccountSerializer(Account.objects.get(password__exact=password))
        except ObjectDoesNotExist:
            password_available = True

        if(username_available and password_available):
            serializer = AccountSerializer(data=body)
            if(serializer.is_valid()):
                serializer.save()
            data = {'responseCode': 0,
                    'responseDesc': 'Success'}
        elif(not username_available and not password_available):
            data = {'responseCode': 3,
                    'responseDesc': 'Username and Password not available'}
        elif(not password_available):
            data = {'responseCode': 2,
                    'responseDesc': 'Password not available'}
        else:
            data = {'responseCode': 1,
                    'responseDesc': 'Username not available'}
        return JsonResponse(data, safe=False)

@csrf_exempt
def account_login(request):
    if request.method == 'POST':
        body = JSONParser().parse(request)
        username = body['username']
        password = body['password']
        print("username: {}, password: {}".format(username, password))
        try:
            account = AccountSerializer(Account.objects.get(username__exact=username))
            if(account.data['password']==password):
                data = {'responseCode': 0,
                        'responseDesc': 'Success',
                        'id':account.data['id'],
                        'username':account.data['username']}
            else:
                data = {'responseCode': 1001,
                        'responseDesc': 'Incorrect password'
                        }
        except ObjectDoesNotExist:
            data = {'responseCode': 404,
                    'responseDesc': 'User not found'}
        return JsonResponse(data, safe=False)

@csrf_exempt
def account_detail(request, pk):
    """
    Retrieve, update or delete a code snippet.
    """
    try:
        account = Account.objects.get(pk=pk)
    except Account.DoesNotExist:
        return HttpResponse(json.dumps({'responseCode':404}),status=404)

    if request.method == 'GET':
        serializer = AccountSerializer(account)
        data = {'responseCode':0,
                'responseDesc':'Success',
                'accounts':serializer.data}
        return JsonResponse(data, safe=False)

    elif request.method == 'PUT':
        data = JSONParser().parse(request)
        serializer = AccountSerializer(account, data=data)
        if serializer.is_valid():
            serializer.save()
            data = {'responseCode': 0,
                    'responseDesc': 'Success'}
            return JsonResponse(data, safe=False)
        return JsonResponse(serializer.errors, status=400)

    elif request.method == 'DELETE':
        account.delete()
        data = {'responseCode': 0,
                'responseDesc': 'Success'}
        return JsonResponse(data, safe=False)