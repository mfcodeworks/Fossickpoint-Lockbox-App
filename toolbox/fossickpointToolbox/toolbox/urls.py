from django.conf.urls import url
from django.contrib.staticfiles.urls import staticfiles_urlpatterns
from fossickpointToolbox import settings
from . import views
from django.conf.urls import include, url
from django.conf.urls.static import static

urlpatterns = [
    url(r'^$', views.index, name="index"),
    url(r'^login/', views.user_login, name="login"),
    url(r'^home/', views.user_home, name="home"),
    url(r'^register/', views.user_register, name="register"),
    url(r'^contents/', views.contents, name="contents"),
    url(r'^content/(?P<contentID>[0-9]+)/$', views.content, name="content"),
    url(r'^users/', views.users, name="users"),
    url(r'^user/(?P<userID>[0-9]+)/$', views.user, name="user"),
    url(r'^programs/', views.programs, name="program"),
    url(r'^program/(?P<programID>[0-9]+)/$',
        views.program, name="programdetail"),
    url(r'logout/', views.logout, name="logout"),
    url(r'^preview/(?P<contentID>[0-9]+)/$', views.preview, name="preview"),
    url(r'^api/login/', views.api_user_validation, name="api_login"),
    url(r'^api/usertype/', views.api_user_type, name="api_usertype"),
    url(r'^category/', views.show_content_category, name="category"),
    url(r'plan/', views.plan, name="adminPlan"),
]

urlpatterns += staticfiles_urlpatterns()
urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
