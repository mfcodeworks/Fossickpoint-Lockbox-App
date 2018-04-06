# -*- coding: utf-8 -*-
# Generated by Django 1.11.5 on 2017-12-17 05:07
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('toolbox', '0026_auto_20171209_2147'),
    ]

    operations = [
        migrations.AddField(
            model_name='category',
            name='parent',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, related_name='children', to='toolbox.Category'),
        ),
    ]
