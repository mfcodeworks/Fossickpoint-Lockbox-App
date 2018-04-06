from django.test import TestCase,Client

# Create your tests here.
class ToolboxTest(TestCase):
    def test_login(self):
        client = Client()
        response = client.post('/login/',{'userName':'dylan','password':'123456'})
        self.assertEqual(response.status_code,200)
        self.assertEqual(response.content,"{'status':0}")

