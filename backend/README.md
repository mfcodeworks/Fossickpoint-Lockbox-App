> tested with python3
# Configuring virtual environment, and installing requirements(Required before running the server).

### Installing virtualenv

```sh
$ pip install virtualenv
```

### Creating virtual environment ".../backend/backend_env" inside ".../backend" project directory
```sh
$ cd backend
$ virtualenv backend_env
```

### To activate virtual environment backend_env
```sh
$ source backend_env/bin/activate
```

### To deactivate virtual environment backend_env
```sh
$ deactivate
```

### Installing requirements.txt(Make sure the virtual environment on ACTIVATED state)
Go to directory ".../backend" that contains requirements.txt
```sh
$ pip install -r requirements.txt
```

# Configure database(Check before running the server)
Requirements, see ".../backend/backend/settings.py":
```sh
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': 'fossickpointdb',
        'PORT': '5432'
    }
}
```
We can see that:
- The database ENGINE is postgresql
- The database NAME is "fossickpointdb" and exist in postgresql database server
- The database is running in PORT 5432
# Running the server(Make sure the virtual environment on ACTIVATED state)



### Running the server
```sh
$ cd backend
$ python3 manage.py runserver
```

# Make migrations after creating new data models in ".../backend/api/models.py" file
```sh
$ cd backend
$ python3 manage.py makemigrations
$ python3 manage.py migrate
```
