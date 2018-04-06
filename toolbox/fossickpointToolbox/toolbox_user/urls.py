from django.conf.urls import url

from fossickpointToolbox import settings
from . import views
from django.conf.urls import include, url
from django.conf.urls.static import static
urlpatterns = [
        url(r'^$', views.index, name="user_login"),
        url(r'^plan/', views.plan, name="plan"),
        url(r'^register/', views.register, name="user_register"),
        url(r'^home/', views.home, name="home"),
        url(r'^login/', views.login, name=None),
        url(r'^contents/(?P<category>[a-zA-Z0-9 \']+)/$', views.contents, name="contents"),
        url(r'^logout/', views.logout, name="user_logout"),
        url(r'^content/(?P<contentID>[0-9]+)/$', views.content, name="user_content"), 
        url(r'^program/(?P<programID>[0-9]+)/$', views.program, name="user_program"),
        url(r'^programlist/', views.programlist,name = "programlist"),
        url(r'^profile/',views.profile, name="profile"),
        url(r'^session/',views.session, name="session"),
        url(r'^search/', views.search, name="search"),

]

urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)