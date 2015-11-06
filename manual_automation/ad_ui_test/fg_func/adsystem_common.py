#encoding:utf8
from fg_func.BaseFunc import *
import conf.config as c
import time
from other_common import *

class AdSystemCommon(BaseFunc):
    def login(self, login_type=1, username='', passwd='', shop_id='', idcode='', supplier='', operator=''):
        if login_type == 1:
            self.goto(c.ADSMART_LOGIN %login_type)
            self.send_keys(PagesElements.LogInPage.USERNAME, username)
            self.send_keys(PagesElements.LogInPage.PASSWORD, passwd)
            self.send_keys(PagesElements.LogInPage.SHOPID, shop_id)
            self.send_keys(PagesElements.LogInPage.IDCODE, idcode)
            self.click(PagesElements.LogInPage.LOGINBUTTON)
        elif login_type == 2:
            self.goto(c.ADSMART_LOGIN %login_type)
            self.send_keys(PagesElements.LogInPage.GYS_ID, supplier)
            self.send_keys(PagesElements.LogInPage.OPERATOR, operator)
            self.send_keys(PagesElements.LogInPage.PASSWORD, passwd)
            self.send_keys(PagesElements.LogInPage.IDCODE, idcode)
            self.click(PagesElements.LogInPage.LOGINBUTTON)
        else:
            self.goto(c.ADSMART_LOGIN %login_type)
            self.send_keys(PagesElements.LogInPage.DDUSER, username)
            self.send_keys(PagesElements.LogInPage.PASSWORD, passwd)
            self.send_keys(PagesElements.LogInPage.IDCODE, idcode)
            self.click(PagesElements.LogInPage.LOGINBUTTON)

    def create_cpc_adgroup_old(self, start_date=None, end_date=None):
        self.login()
        self.goto(c.CPC_GROUP_ADD)
        self.send_keys(PagesElements.AddCpcGroupPage.GROUP_NAME, u"old广告组"+str(time.strftime('%m%d%H%M%S')))

        if start_date and end_date:
            if date_format_to_stamp2(start_date) < date_formate_to_stamp_today():
                raise "start date cannot ealier than today"
            elif date_format_to_stamp2(end_date) < date_format_to_stamp2(start_date):
                raise "end date cannot ealier than start date"
            self.removeReadOnly(PagesElements.AddCpcGroupPage.START_DATE_INPUT)
            self.send_keys(PagesElements.AddCpcGroupPage.START_DATE_INPUT, start_date)
            self.click(PagesElements.AddCpcGroupPage.HAS_END_DATE_DADIO)
            self.removeReadOnly(PagesElements.AddCpcGroupPage.END_DATE_INPUT)
            self.send_keys(PagesElements.AddCpcGroupPage.END_DATE_INPUT, end_date)
        elif start_date and not end_date:
            self.removeReadOnly(PagesElements.AddCpcGroupPage.START_DATE_INPUT)
            self.send_keys(PagesElements.AddCpcGroupPage.START_DATE_INPUT, start_date)
        elif end_date and not start_date:
            self.click(PagesElements.AddCpcGroupPage.HAS_END_DATE_DADIO)
            self.removeReadOnly(PagesElements.AddCpcGroupPage.END_DATE_INPUT)
            self.send_keys(PagesElements.AddCpcGroupPage.END_DATE_INPUT, end_date)

        self.click(PagesElements.AddCpcGroupPage.CONFIRM_BUTTON)
        self.quit()

    def create_personal_ad(self):
        self.login()
        self.goto(c.PERSONAL_LIST_PAGE)
        random.choice(self.get_a_in_list("xpath->//li//a", "推广")).click()
        self.send_keys(self.CreatePersonalAdPage.TITLE1, u'autoPad'+str(time.strftime('%m%d%H%M%S')))
        self.send_keys(self.CreatePersonalAdPage.DAILY_LIMIT, '35')
        self.select(self.CreatePersonalAdPage.SELECT_GROUP, 1)
        self.submit(self.CreatePersonalAdPage.SUBMIT)
        self.quit()


