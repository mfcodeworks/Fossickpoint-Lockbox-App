from . import views
from django.conf.urls import include, url

urlpatterns = [
    url(r'^register/', views.register, name="register"),
    url(r'^login/', views.login, name="login"),
    url(r'^redeemcode/', views.redeem_code, name="redeemcode"),
    url(r'^profile_detail/', views.profile_detail, name="profile_detail"),
    url(r'^available_schedule/', views.get_available_schedule, name="available_schedule"),
    url(r'^post_schedule/', views.post_schedule, name="post_schedule"),
    url(r'^user_schedule/', views.get_schedule_for_user, name="user_schedule"),
    url(r'^cancel_schedule/', views.cancel_schedule, name="cancel_schedule")
]