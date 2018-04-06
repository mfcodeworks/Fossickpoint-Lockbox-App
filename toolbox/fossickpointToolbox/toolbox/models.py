# -*- coding: utf-8 -*-
from django.db import models
from mptt.models import MPTTModel, TreeForeignKey
# Create your models here.

# user model
class User(models.Model):
    userName = models.CharField(max_length=30)
    userPassword = models.CharField(max_length=30)
    # todo
    # add email
    userType = models.IntegerField(default=1)  #0: admin 1:unactivated 2: trail 3: unlimited 4: personalized
    toolbox_user_type = models.IntegerField(default=1) #1: free plan; 2: unlimited plan; 3: individualized plan; 4； premium plan
    def __str__(self):
        return self.userName

 # profile model
class Profile(models.Model):
    profileName = models.TextField(max_length=100)

    def __str__(self):  # __unicode__ on Python 2
        return self.profileName

    class Meta:
        ordering = ('profileName',)

# program model
class Program(models.Model):
    name = models.CharField(max_length=100)
    describe = models.CharField(max_length=200)
    contentsNumber = models.IntegerField(default=0)

    def __str__(self):
        return self.name
# user personal info model
class PersonalInfo(models.Model):
    user = models.ForeignKey(User, on_delete = models.CASCADE)
    gender = models.IntegerField(default=0)  #0:male 1:female
    age = models.IntegerField(default=20)
    hobby = models.CharField(max_length=100)
    email = models.EmailField()
    name = models.CharField(max_length=100,default='')
    note = models.CharField(max_length=300,null=True, blank=True)
    nation = models.CharField(max_length=100,null=True, blank=True)
    city = models.CharField(max_length=100,null=True, blank=True)
    occupation = models.CharField(max_length=100,null=True, blank=True)
    address = models.CharField(max_length=200,null=True, blank=True)
    programs = models.ManyToManyField(Program)
    profile = models.ManyToManyField(Profile)
    coupon_code = models.CharField(max_length=10, null=True, blank=True)
    toolbox_certificate_code = models.CharField(max_length=10, null=True, blank=True)
    # todo add toolbox_rest_session to show the number of seesions user can book now
    def __str__(self):
        return self.user.userName

# content category
# todo enable sub category it is difficult in showing the structure on the HTML page.
# class Category(models.Model):
#     name = models.CharField(max_length=100)
#     parent = models.ForeignKey('self', blank=True, null=True, related_name="children")
#     def __str__(self):
#         return self.name


# mptt model to implement category
class ContentCategory(MPTTModel):
    name = models.CharField(max_length=200, unique=True)
    parent = TreeForeignKey('self', null=True, blank=True, related_name='children', db_index=True, on_delete = models.DO_NOTHING)
    # todo 
    image = models.ImageField(upload_to='categories/', default='categories/default.jpg')
    def __str__(selfs):
        return selfs.name
    def image_tag(self):
        return u'<img src="%s" />' % self.image
    def __iter__(self):
        yield 'name', self.name
        yield 'image', self.image.url.split("/")[-1]
    image_tag.short_description = 'Image'
    image_tag.allow_tags = True
    class MPTTMeta:
        order_insertion_by = ['name']


# label model  another way to implement multi-level categories
# class Labels(models.Model):
#     category = models.ForeignKey(Category)
#     name = models.CharField(max_length=200)
# content model
class Content(models.Model):
    name = models.CharField(max_length=100)
    type_choice = (('doc','doc'),('pdf','pdf'),('image', 'image'),('video', 'video'),('audio', 'audio'),('other','other'))
    type = models.CharField(max_length=30,choices=type_choice)
    focus_choice = (('Emotional Intelligence','Emotional Intelligence'),('Social Intelligence','Social Intelligence'),('Self Actualization','Self Actualization'))
    focus = models.CharField(max_length=30,choices=focus_choice,default='Emotional Intelligence')
    tag_choice = (('Images','Images'),('Formulas','Formulas'),('Activities','Activities'),('Skills','Skills'),('Social frameworks','Social frameworks'),('Concepts','Concepts'),('Myths & notions','Myths & notions'),('Principles','Principles'))
    tag = models.CharField(max_length=30,choices=tag_choice)
    thumbnail = models.ImageField(upload_to='./', null=True)
    profile = models.ManyToManyField(Profile)
    profileText = models.TextField(max_length=100,default='')
    keyword = models.CharField(max_length=100)
    address = models.FileField(upload_to='./')
    category = models.ForeignKey(ContentCategory, blank='True', null='True', on_delete=models.SET_NULL)
    # labels = models.ManyToManyField(Labels, blank='True', null='True')
    # define content level corresponding to user type
    level = models.IntegerField(default=1) # 1: free content; 2: limited; 3: personlized
    def __str__(selfs):
        return selfs.name
    # convert the model to dict
    def __iter__(self):
        yield 'name', self.name
        yield 'level', self.level
        yield 'focus', self.focus
        yield 'tag', self.tag
        yield 'thumbnail', self.thumbnail.url.split("/")[-1]
        yield 'id', self.id
        yield 'profileText', self.profileText
        yield 'keyword', self.keyword
        yield 'profile', self.profile.all()
        yield 'address', self.address.url
        yield 'description', self.keyword
        yield 'format', self.type
        yield 'category', self.category


# doesn't need now
class Group(models.Model):
    name = models.CharField(max_length=30)
    contents = models.TextField(max_length=None)
    def __str__(selfs):
        return selfs.name

# doesn't need now
class UserContent(models.Model):
    user = models.ForeignKey(User, on_delete = models.DO_NOTHING)
    contents = models.TextField(max_length=None)
    def __str__(selfs):
        return selfs.user.name


# program detail model
class ProgramDetail(models.Model):
    content = models.ForeignKey(Content, on_delete = models.DO_NOTHING)
    program = models.ForeignKey(Program, on_delete = models.DO_NOTHING)
    order = models.IntegerField(null=True, blank=True)
    def __str__(self):
        return self.content.name

# toolbox plan model
class Plan(models.Model):
    name = models.CharField(max_length = 100)
    price = models.CharField(max_length= 20)
    description = models.TextField(max_length=None)
    plan_acuity_package_link = models.URLField(blank=True, default="")
    plan_acuity_appointment_id = models.CharField(max_length=20, blank=True, default="")
    plan_id = models.IntegerField(default=0)
    def __str__(self):
        return self.name

    def __iter__(self):
        yield 'name', self.name,
        yield 'price', self.price,
        yield 'plan_acuity_package_link', self.plan_acuity_package_link,
        yield 'description', self.description.split(';')
