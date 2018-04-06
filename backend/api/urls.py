from django.conf.urls import url
from rest_framework.urlpatterns import format_suffix_patterns

from api import views

urlpatterns = {
    url(r'^account/$', views.account_list, name="accountlist"),
    url(r'^account/(?P<pk>[0-9]+)/$', views.account_detail, name="accountdetail"),
    url(r'^account/login', views.account_login, name="accountlogin"),
    url(r'^account/register', views.account_register, name="accountregister")

}

urlpatterns = format_suffix_patterns(urlpatterns)