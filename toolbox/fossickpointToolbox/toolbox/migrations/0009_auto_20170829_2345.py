# -*- coding: utf-8 -*-
# Generated by Django 1.11.3 on 2017-08-29 23:45
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('toolbox', '0008_auto_20170825_0047'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='content',
            name='nextContent',
        ),
        migrations.RemoveField(
            model_name='content',
            name='preContent',
        ),
        migrations.AddField(
            model_name='content',
            name='focus',
            field=models.CharField(choices=[('Being', 'Emotional Intelligence'), ('Belonging', 'Social Intelligence'), ('Becoming', 'Self Actualization')], default='Emotional Intelligence', max_length=30),
        ),
        migrations.AddField(
            model_name='content',
            name='profile',
            field=models.CharField(default='', max_length=100),
        ),
        migrations.AlterField(
            model_name='content',
            name='tag',
            field=models.CharField(choices=[('Images', 'Images'), ('Formulas', 'Formulas'), ('Activities', 'Activities'), ('Skills', 'Skills'), ('Social frameworks', 'Social framworks'), ('Concepts', 'Concepts'), ('Myths & notions', 'Myths & notions'), ('Principles', 'Principles')], max_length=30),
        ),
    ]