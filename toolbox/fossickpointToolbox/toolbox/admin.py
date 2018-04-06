from django.contrib import admin
from django.utils.html import format_html
from mptt.admin import MPTTModelAdmin
from .models import User, PersonalInfo, Content, UserContent,Profile,Program,ProgramDetail,ContentCategory,Plan
# Register your models here.
admin.site.register(User)
admin.site.register(PersonalInfo)
admin.site.register(Content)
admin.site.register(UserContent)
admin.site.register(Profile)
admin.site.register(Program)
admin.site.register(ProgramDetail)
admin.site.register(ContentCategory, MPTTModelAdmin)
admin.site.register(Plan)

class Model1Admin(admin.ModelAdmin):

    def image_tag(self, obj):
        return format_html('<img src="{}" />'.format(obj.thumbnail))

    image_tag.short_description = 'Image'

    list_display = ['image_tag',]



