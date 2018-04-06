from django.db import models

# Create your models here.
class Account(models.Model):
    username = models.CharField(max_length=255, blank=False)
    password = models.CharField(max_length=255, blank=False)
    date_created = models.DateTimeField(auto_now_add=True)
    date_modified = models.DateTimeField(auto_now=True)

    def __str__(self):
        return "{}".format(self.venue_name)