from django import forms
from .models import Content
class UploadFileForm(forms.Form):
    title = forms.CharField(max_length=50)
    type = forms.CharField(max_length=50)
    tag = forms.CharField(max_length=50)
    keyword = forms.CharField(max_length=50)
    file = forms.FileField()

class ContentForm(forms.Form):
    class Meta:
        model = Content
        fields = ['name','type','focus','tag','keyword','profile']