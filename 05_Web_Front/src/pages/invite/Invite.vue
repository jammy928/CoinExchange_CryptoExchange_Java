
<template>
  <div class="invite">
    <div class="header">
      <div class="invite-title">
        <div class="my-link-text">{{$t('invite.myinvitelink')}}：</div>
        <div class="my-link-content">{{myInfo.inviteLink}}</div>
        <a class="copy-button" v-clipboard:copy="myInfo.inviteLink" v-clipboard:success="onCopy" v-clipboard:error="onError" href="javascript:;">{{$t('invite.copy')}}</a>
      </div>
      <div class="invite-detail">
        <div class="item">
          <p class="i-v">{{myInfo.levelone}}</p>
          <p class="i-t">{{$t('invite.mylevelone')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.leveltwo}}</p>
          <p class="i-t">{{$t('invite.myleveltwo')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.commission}}</p>
          <p class="i-t">{{$t('invite.mycommission')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.extrareward}}</p>
          <p class="i-t">{{$t('invite.extrareward')}}</p>
        </div>
        <div class="item">
          <p class="i-v">{{myInfo.partnerlevel}}</p>
          <p class="i-t">{{$t('invite.partnerlevel')}}</p>
        </div>
      </div>
      <div class="qr-code" style="background:#FFF;padding: 5px 5px;">
        <qriously :value="myInfo.inviteLink" :size="140" foreground="#000" />
      </div>

      <div class="mask" v-if="!isLogin">
        <div class="login-btn">
              <ButtonGroup size="large">
                  <Button type="warning" to="/login">{{$t("common.login")}}</Button>
                  <Button type="warning" to="/register">{{$t("common.register")}}</Button>
              </ButtonGroup>
        </div>
      </div>
    </div>
    <p class="headertip">* {{$t('invite.headertip')}}</p>
    <div class="main">
      <div class="invite-content">
        <h2>{{$t('invite.ruledetail')}}</h2>
        <div class="content">
          <p style="margin-bottom:20px;border-left: 5px solid #e7e7e7;padding-left: 25px;">{{$t('invite.ruleprofile1')}}<b>{{$t('invite.ruleprofile2')}}</b><b>{{$t('invite.ruleprofile3')}}</b>{{$t('invite.ruleprofile4')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
          <table class="percent-table">
            <tr style="background:#EDEDED;">
            <td>{{$t('invite.level')}}</td><td>{{$t('invite.levelonecount')}}</td><td>{{$t('invite.levelonecommission')}}</td>
            <td>{{$t('invite.leveltwocommission')}}</td><td>{{$t('invite.partnerdivide')}}</td></tr>

            <tr><td>L1({{$t('invite.lv1')}})</td><td>0 ≤ N ≤ 10</td><td>20% / 6{{$t('invite.month')}}</td><td>5% / 6{{$t('invite.month')}}</td><td></td></tr>
            <tr><td>L2({{$t('invite.lv2')}})</td><td>10 < N ≤ 100</td><td>30% / 12{{$t('invite.month')}}</td><td>10% / 12{{$t('invite.month')}}</td><td></td></tr>
            <tr><td>L3({{$t('invite.lv3')}})</td><td>100 < N ≤ 500</td><td>40% / 12{{$t('invite.month')}}</td><td>15% / 12{{$t('invite.month')}}</td><td></td></tr>
            <tr><td>L4({{$t('invite.lv4')}})</td><td>500 < N ≤ 1500</td><td>50% / 24{{$t('invite.month')}}</td><td>20% / 24{{$t('invite.month')}}</td><td>+5%</td></tr>
            <tr><td>L5({{$t('invite.lv5')}})</td><td>1500 < N ≤ 3000</td><td>60% / 24{{$t('invite.month')}}</td><td>25% / 24{{$t('invite.month')}}</td><td>+10%</td></tr>
            <tr><td>L6({{$t('invite.lv6')}})</td><td>3000 < N</td><td>60% / {{$t('invite.alllife')}}</td><td>25% / {{$t('invite.alllife')}}</td><td>+15%</td></tr>
          </table>
          <div style="font-size: 12px;padding: 10px;margin-left: 30px;width: 90%;margin-bottom: 10px;border: 1px solid #c8c8c8;">
            <p style="font-size: 13px;font-weight:bold;">{{$t('invite.ruleexampletitle')}}</p>
            <p style="text-indent: 25px;">    {{$t('invite.ruleexamplecontent1')}}</p>
            <p style="text-indent: 25px;">    {{$t('invite.ruleexamplecontent2')}}</p>
          </div>
          <p>{{$t('invite.ruletext8')}}</p>
          <p>{{$t('invite.ruletext9')}}</p>
          <p>{{$t('invite.ruletext10')}}</p>
          <p>{{$t('invite.ruletext11')}}</p>
          <p class="rule-update">{{$t('invite.lastupdate')}}：2019/07/18 12:15:34</p>
        </div>
      </div>
      <div class="promotion-tools">
        <h2>{{$t('invite.ptools')}}</h2>
        <div class="tools">
          <div class="item" @click="showCardItem">
              <img src="https://bizzan.oss-cn-hangzhou.aliyuncs.com/2019/promotioncard.png"></img>
              <p class="title">{{$t('invite.pt_title')}}<span style="color:#FF0000;margin-left:10px;font-size:13px;">{{$t('invite.pt_card_title_tips')}}</span></p>
              <p class="desc">{{$t('invite.pt_desc')}}</p>
          </div>
          <div class="item" @click="useInviteImage">
              <img src="https://bizzan.oss-cn-hangzhou.aliyuncs.com/2019/invitebg.jpg"></img>
              <p class="title">{{$t('invite.pt_invite_title')}}</p>
              <p class="desc">{{$t('invite.pt_invite_desc')}}</p>
          </div>
          <div class="more">
            <p style="color:#c2c2c2;font-size:16px;">{{$t('invite.pt_more')}}</p>
          </div>
        </div>
        <p class="bottom">{{$t('invite.pt_tips')}}</p>
      </div>
      <div class="invite-rank">
        <Row :gutter="80">
          <Col span="12">
            <h2>{{$t('invite.totalcommissionrank')}}TOP20{{$t('invite.rankall')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastUpdate}}</span></p>
            <Table class="rank-table" :columns="columns" :data="topRewardRank"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip1')}}</p>
          </Col>
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankall')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastUpdate}}</span></p>
            <Table class="rank-table" :columns="columns" :data="topInviteCountRank"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
        </Row>
      </div>

      <div class="invite-rank" style="display:none;">
        <Row :gutter="80">
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankweek')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastStasticWeek}}</span></p>
            <Table class="rank-table" :columns="columns1" :data="topRankWeek"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
          <Col span="12">
            <h2>{{$t('invite.promotioncount')}}TOP20{{$t('invite.rankmonth')}}</h2>
            <p class="ranktitle"><span>{{$t('invite.laststastic')}}: {{lastStasticMonth}}</span></p>
            <Table class="rank-table" :columns="columns1" :data="topRankMonth"></Table>
            <p class="rank-tips">* {{$t('invite.ranktip2')}}</p>
          </Col>
        </Row>
      </div>
    </div>
    <H1 style="color:#000;text-align:center;margin-top: 100px;font-size:30px;;">{{$t('invite.thanks')}}</H1>

    <Drawer :title="$t('invite.pt_title')" :closable="false" v-model="showCardModal" width="600">
      <div class="ptcard-header">
        <img class="card-img" src="https://bizzan.oss-cn-hangzhou.aliyuncs.com/2019/promotioncard.png"></img>
        <div class="desc">
          <p class="title">{{$t('invite.pt_title')}}</p>
          <p class="amount">{{$t('invite.pt_card_amount')}}：0.001 BTC</p>
          <p class="deadline">{{$t('invite.pt_card_deadline')}}：60{{$t('invite.pt_card_day')}}</p>

          <Button v-if="isLogin && !hasCard" type="error" size="large" @click="getFreeCard">{{$t('invite.pt_card_btn')}}</Button>
          <Button v-if="isLogin && hasCard" type="error" size="large" @click="usePromotionImage">{{$t('invite.usepromotion')}}</Button>
          <Button v-else to="/login" type="success" size="large">{{$t('invite.pt_card_btn_login')}}</Button>
        </div>
      </div>
      <Divider style="margin-top: 40px;" orientation="left">{{$t('invite.pt_card_rule')}}</Divider>
      <div class="ptcard-info">
        <p class="title">{{$t('invite.pt_card_summary')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule1')}}：promotion@bizzan.com。</p>
        <p class="detail">{{$t('invite.pt_card_rule2')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule3')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule4')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule5')}}</p>
        <p class="detail">{{$t('invite.pt_card_rule6')}}</p>
      </div>
    </Drawer>

    <Drawer :title="promotionTitle" :closable="false" v-model="showPromotionModal" width="350" style="text-align:center;">
      <div style="position:relative;width: 318px;" id="promotionImage" ref="promotionImage">
        <img style="width:100%;display:block;" src="../../assets/images/promotion/promotionbg1.jpg"></img>
        <p style="position:absolute;top: 210px;text-align:center;width: 100%;text-align:center;font-size:26px;color:#F90;font-weight:bold;">{{promotionCode}}</p>
        <p style="position:absolute;top: 250px;text-align:center;width: 100%;text-align:center;">推广合伙人专属兑换码</p>
      </div>
      <p style="text-align:center;font-size:12px;color:#888;margin-top: 10px;">{{$t('invite.imagetips')}}</p>
      <Button type="error" size="large" :loading="saveImageLoading" long style="margin-top: 20px;" @click="saveImage">{{$t('invite.saveimage')}}</Button>
    </Drawer>

    <Drawer title="邀请图片" :closable="false" v-model="showInviteImageModal" width="350" style="text-align:center;">
      <div style="position:relative;width: 318px;" id="inviteImage" ref="inviteImage">
        <img style="width:100%;display:block;" src="../../assets/images/promotion/invitebg1.jpg"></img>
        <div class="qr-code" style="background:#FFF;position:absolute;top: 260px;left:105px;border-radius: 5px;height: 100px;">
          <qriously :value="myInfo.inviteLink" :size="100" foreground="#000" />
        </div>
        <p style="position:absolute;top: 375px;text-align:center;width: 100%;text-align:center;">扫一扫了解更多</p>
        <p style="position:absolute;top: 395px;text-align:center;width: 100%;text-align:center;font-size:16px;">我的邀请码：{{inviteCode}}</p>
      </div>
      <p style="text-align:center;font-size:12px;color:#888;margin-top: 10px;">{{$t('invite.imagetips')}}</p>
      <Button type="error" size="large" :loading="saveImageLoading" long style="margin-top: 20px;" @click="saveInviteImage">{{$t('invite.saveimage')}}</Button>
    </Drawer>
  </div>
</template>

<script>
import Vue from "vue";
import VueQriously from "vue-qriously";
import html2canvas from 'html2canvas';

Vue.use(VueQriously);

export default {
  data() {
    let self = this;
    return {
      showCardModal: false,
      hasCard: false,
      showPromotionModal: false,
      saveImageLoading: false,
      showInviteImageModal: false,
      promotionTitle: "",
      inviteCode: "", // 邀请码
      promotionCode: "", // 兑换码
      myInfo: {
        levelone: 0,
        leveltwo: 0,
        commission: 0,
        extrareward: 0,
        partnerlevel: "-",
        inviteLink: "https://www.bizzan.com/register?code=000000"
      },
      promotionRecordPage: {
        pageNo: 1,
        pageSize: 10
      },
      columns: [
          {
              title: self.$t("invite.colum_text0"),
              type: "index",
              width: 60,
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text1"),
              key: 'userIdentify',
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text2"),
              key: 'levelOne',
              width: 110,
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text3"),
              key: 'estimatedReward',
              width: 160
          },
          {
              title: self.$t("invite.colum_text4"),
              width: 90,
              key: 'extraReward'
          }
      ],
      topRankMonth:[],
      topRankWeek: [],
      lastStasticWeek: "",
      lastStasticMonth: "",
      columns1: [
          {
              title: self.$t("invite.colum_text0"),
              type: "index",
              width: 80,
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text1"),
              key: 'userIdentify',
              align: 'center'
          },
          {
              title: self.$t("invite.colum_text2"),
              key: 'levelOne',
              align: 'center'
          }
      ],
      lastUpdate: "",
      topRewardRank: [],
      topInviteCountRank: [],
      dataFanyong: [
          {
              sort: 1,
              name: '186****9837',
              age: 18,
              address: '18273.98',
              date: '+23'
          },
          {
              sort: 2,
              name: '186****9837',
              age: 24,
              address: '18273.98',
              date: '+23'
          }
      ],
      dataFanyong1: [
          {
              sort: 1,
              name: '186****9837',
              age: 18,
              address: '18273.98',
              date: '+23'
          }
      ]
    };
  },
  created: function() {
    this.init();
  },
  computed: {
    lang() {
      this.updateLangData();
      return this.$store.state.lang;
    },
    langPram(){
      if(this.$store.state.lang == "简体中文"){
        return "CN";
      }
      if(this.$store.state.lang == "English"){
        return "EN";
      }
      return "CN";
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-invite");
      if(this.isLogin){
        this.getMemberInfo();
        this.getCardInfo();
      }
      this.getRank();
    },
    useInviteImage(){
      if(!this.isLogin) {
        this.$Message.error(this.$t("common.logintip"));
        return;
      }
      this.showInviteImageModal = true;
      window.pageYOffset = 0;
      document.documentElement.scrollTop = 0;
      document.body.scrollTop = 0;
    },
    usePromotionImage(){
      if(!this.isLogin) {
        this.$Message.error(this.$t("common.logintip"));
        return;
      }
      this.showPromotionModal = true;
      window.pageYOffset = 0;
      document.documentElement.scrollTop = 0;
      document.body.scrollTop = 0;
    },
    getCardInfo(){
      this.$http.post(this.host + this.api.uc.mycardlist).then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          if(resp.data != null && resp.data.length > 0){
            this.promotionTitle = resp.data[0].cardName;
            this.promotionCode = resp.data[0].cardNo;
            this.hasCard = true;
          }
        } else {
          this.$Message.error(this.loginmsg);
        }
        this.loading = false;
      });
    },
    dataURLToBlob(dataurl) {
        let arr = dataurl.split(',');
        let mime = arr[0].match(/:(.*?);/)[1];
        let bstr = atob(arr[1]);
        let n = bstr.length;
        let u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], { type: mime });
    },
    saveInviteImage(){
      this.save("inviteImage", "邀请图片");
      this.saveImageLoading = true;
    },
    saveImage(){
      this.save("promotionImage", "推广合伙人图片");
      this.saveImageLoading = true;
    },
    save(divText, imgText) {
        let canvasID = this.$refs[divText];
        let that = this;
        let scale = 1;
        let a = document.createElement('a');
        html2canvas(canvasID, {
          scale,
          useCORS:true,
          width: canvasID.offsetWidth * scale,
          height: canvasID.offsetHeight * scale
        }).then(canvas => {
            const context = canvas.getContext('2d');
            // 【重要】关闭抗锯齿 https://segmentfault.com/a/1190000011478657
            context.mozImageSmoothingEnabled = false;
            context.webkitImageSmoothingEnabled = false;
            context.msImageSmoothingEnabled = false;
            context.imageSmoothingEnabled = false;
            context.scale(scale, scale);

            let dom = document.body.appendChild(canvas);
            dom.style.display = 'none';
            a.style.display = 'none';
            document.body.removeChild(dom);
            let blob = that.dataURLToBlob(dom.toDataURL('image/png'));
            a.setAttribute('href', URL.createObjectURL(blob));
            //这块是保存图片操作  可以设置保存的图片的信息
            a.setAttribute('download', imgText + '.png');
            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(blob);
            document.body.removeChild(a);

            this.saveImageLoading = false;
        });
    },
    showCardItem(){
      this.showCardModal = true;
    },
    onCopy(e) {
      this.$Message.success(
        this.$t("uc.finance.recharge.copysuccess") + e.text
      );
    },
    onError(e) {
      this.$Message.error(this.$t("uc.finance.recharge.copysuccess"));
    },
    getMemberInfo(){
      this.myInfo.inviteLink = this.$store.state.member.promotionPrefix + this.$store.state.member.promotionCode;
      this.inviteCode = this.$store.state.member.promotionCode;

      // 获取邀请人数
      this.$http
        .post(this.host + this.api.uc.memberInfo, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.myInfo.levelone = resp.data.firstLevel;
            this.myInfo.leveltwo = resp.data.secondLevel;
            this.myInfo.partnerlevel = this.partnerNameByCount(resp.data.firstLevel);
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });

      // 获取个人佣金信息
      this.$http
        .post(this.host + this.api.uc.mypromotion, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.myInfo.commission = resp.data.estimatedReward;
            this.myInfo.extrareward = resp.data.extraReward;
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });
    },
    getFreeCard: function(){
      this.$http
        .post(this.host + this.api.uc.getfreecard, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Notice.success({
              title: this.$t("common.tip"),
              desc:this.$t("invite.pt_card_receivew_success"),
              duration: 30
            });

            this.getCardInfo();
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message,
              duration: 15
            });
          }
        });
    },
    getTopRankWeek: function(){
      // 获取推广合伙人排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotionweek, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRankWeek = resp.data.topinviteweek;
            if(this.topRankWeek.length > 0) {
              this.lastStasticWeek = this.topRankWeek[0].stasticDate;
            }
          }
        });
    },
    getTopRankMonth: function(){
      // 获取推广合伙人排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotionmonth, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRankMonth = resp.data.topinvitemonth;
            if(this.topRankMonth.length > 0) {
              this.lastStasticMonth = this.topRankMonth[0].stasticDate;
            }
          }
        });
    },
    getRank: function(){
      // 获取推广合伙人排行榜
      this.$http
        .post(this.host + this.api.uc.toppromotion, {})
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.topRewardRank = resp.data.topreward;
            this.topInviteCountRank = resp.data.topinvite;
            if(this.topRewardRank.length > 0) {
              this.lastUpdate = this.topRewardRank[0].stasticDate;
            }
          } else {
            this.$Notice.error({
              title: this.$t("common.tip"),
              desc:resp.message
            });
          }
        });
    },
    partnerNameByCount: function(count) {
      if(count == undefined || count == null || count == ""){
        return "-";
      }
      let partnerName = "Lv1(" + this.$t('invite.lv1') +")";
      if(count > 10) partnerName = "Lv2(" + this.$t('invite.lv2') +")";
      if(count > 100) partnerName = "Lv3(" + this.$t('invite.lv3') +")";
      if(count > 500) partnerName = "Lv4(" + this.$t('invite.lv4') +")";
      if(count > 1500) partnerName = "Lv5(" + this.$t('invite.lv5') +")";
      if(count > 3000) partnerName = "Lv6(" + this.$t('invite.lv6') +")";
      return partnerName;
    },
    updateLangData: function(){
      this.columns[0].title = this.$t("invite.colum_text0");
      this.columns[1].title = this.$t("invite.colum_text1");
      this.columns[2].title = this.$t("invite.colum_text2");
      this.columns[3].title = this.$t("invite.colum_text3");
      this.columns[4].title = this.$t("invite.colum_text4");
    }
  }
};
</script>
<style lang="scss" scoped>
.invite {
  display:flex;
  flex-direction: column;
  background: linear-gradient(200deg, #f19100, #ffc84b, #ffc540);
  padding: 0 12%;
  padding-bottom: 100px;
  min-height: 400px;
  .header{
    position: relative;
    margin-top: 100px;
    padding: 30px 50px;
    width:100%;
    height: 180px;
    color: #000;
    background: linear-gradient(200deg, #ff9900, #ffbe2b, #ffa500);
    background-size: 100% 100%;
    box-shadow: 0px 0px 20px #5b5b5b8c;
    margin-bottom: 10px;
    border-radius: 8px;
    .invite-title{
      font-size: 18px;letter-spacing: 2px;display:flex;
      flex-direction: row;
      margin-right: 170px;
      padding-bottom: 15px;
      border-bottom: 1px dashed #e3a700;
      .my-link-text{
        height: 38px;line-height: 38px;
      }
      .my-link-content{
        background:rgba(0, 0, 0, 0);border-radius: 3px;height: 38px;line-height: 38px;width: 600px;text-align: center;
        box-shadow: 0px 0px 15px #00000026 inset;
      }
      .copy-button{
        height:32px;line-height:32px;background:linear-gradient(350deg, #e1e1e1, #ffffff);padding:0 20px;margin-left: 20px;border-radius: 5px;color: #000;font-size: 14px;margin-top:3px;box-shadow: 0px 0px 10px #afafaf;width:100px;text-align:center;
        cursor: pointer;
        &:hover{
          background:linear-gradient(350deg, #efefef, #e1e1e1);
        }
        &:focus{
          box-shadow: 0px 0px 10px #00000026 inset;
        }
      }
    }
    .invite-detail{
      margin-right: 170px;
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      padding-top: 10px;
      .item{
        width: 18%;
        text-align:center;
        .i-t{
          font-size: 12px;
          background: #0000000f;
          padding: 5px 0;
        }
        .i-v{
          font-size: 26px;
          font-weight: bold;
        }
      }
    }
    .qr-code{
      position: absolute;
      top: 15px;
      right: 20px;
      height: 150px;
      width: 150px;
      border-radius: 10px;
      box-shadow: 0px 0px 15px #00000057 inset;
    }
    .mask{
      width: 100%;
      background: #ffffffe0;
      position: absolute;
      top: 0;
      left: 0;
      border-radius: 8px;
      height: 180px;
      text-align: center;
      .login-btn{
        width: 150px;
        height: 35px;
        line-height: 35px;
        border-radius: 3px;
        margin: 0 auto;
        margin-top: 80px;
      }
    }
  }
  p.headertip{
    font-size:12px;text-align:right;color:rgba(0, 0, 0, 0.48);text-shadow: 0 0 25px #ffffff;
  }
  .main {
    min-height: 300px;
    width: 100%;
    margin: 0 auto;
    border-radius: 6px;
    margin-top: 30px;
    .invite-content{
      background: #FFF;
      padding: 30px 30px;
      margin-bottom: 30px;
      color: #6d6d6d;
      border-radius: 5px;
      box-shadow: 0px 0px 20px #909090;
      .content{
        margin-top: 20px;
        min-height: 100px;
        padding-left: 40px;
        line-height: 30px;
        letter-spacing: 1px;
        .percent-table{
          margin-bottom: 10px;
          background:#EDEDED;
          text-align:center;margin-left: 30px;font-size:12px;width:90%;margin-top: 10px;
          border-right: 1px solid #c8c8c8;
          border-bottom: 1px solid #c8c8c8;
          border-collapse:collapse;
          tr{
            background:#FFF;
            td{
              border-left: 1px solid #c8c8c8;
              border-top:1px solid #c8c8c8;
            }
          }
        }
        .rule-update{
          font-size: 12px;text-align:right;color:#AFAFAF;margin-top: 20px;padding-top: 20px;border-top: 1px solid #EDEDED;
        }
      }
      >h2{
        padding-bottom: 10px;
        border-bottom: 1px solid #EDEDED;
      }
    }
    .promotion-tools{
      color: #3a3a3a;
      position:relative;
      h2{
        padding-bottom: 0px;
        text-align:left;
        margin-bottom: 20px;
        margin-top: 60px;
      }
      .tools{
        min-height: 250px;
        background: #FFF;
        border-radius: 5px;
        box-shadow: 0px 0px 20px #909090;
        padding: 20px 20px 50px 20px;
        display: flex;
        flex-direction: row;
        .item{
          width: 310px;padding: 5px;
          img{
            width: 300px;border-radius: 5px;height: 160px;
            &:hover{
              opacity:0.9;
            }
          }
          p.title{
            font-size:16px;padding: 0 5px;
          }
          p.desc{
            font-size:12px;padding: 0 5px;color: #999;
          }
          &:hover{
            cursor:pointer;
          }
        }
      }
      .more{
        margin: 5px 20px;
        border-radius: 5px;
        line-height: 153px;
        width: 300px;
        height: 160px;
        padding: 5px;
        background: #EEE;
        text-align: center;
      }
      .bottom{
        color:#C2C2C2;font-size:12px;text-align:right;margin-top: 10px;position:absolute;bottom: 5px;position:absolute;width:96%;bottom: 10px;left:2%;
        border-top: 1px dashed #C2C2C2;padding-top: 10px;text-align:center;
      }
    }
    .invite-rank{
      margin-top: 60px;
      p.ranktitle{
        text-align:left;font-size:12px;margin-bottom: 20px;color:#383838;
      }
      h2{
        padding-bottom: 0px;
        color: #3a3a3a;
        text-align:left;
        span{
          font-size: 14px;font-weight:normal;
          margin-left: 10px;
        }
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row td{
        background-color: transparent;
        border: none;
        border-bottom: 1px solid #e3e3e3;
        color: #151515;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-header th{
        background-color: #F90;
        color: #000;
      }
      .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
        background: #f7f7f7!important;
      }
      .rank-tips{
        text-align:right;color:#000;font-size:12px;margin-top:10px;
      }
      .rank-table{
        box-shadow: 0 0 25px #000;
      }
    }
  }
}

.ptcard-header{
  display:flex;flex-direction:row;justify-content:flex-start;margin-top:20px;
  .card-img{
    width:260px;height:140px;border-radius:5px;
  }
  .desc{
    height:130px;width:100%;color:#000;padding-left: 20px;
    .title{
      font-size:20px;margin-bottom:5px;
    }
    .amount, .deadline{
      font-size:14px;color: #888;
    }
    .ivu-btn{
      margin-top:25px;
      letter-spacing: 1px;
    }
  }
}
.ptcard-info{
  padding-left: 40px;color:#555;line-height:25px;padding-right:20px;
  .title{
    color: rgb(85, 85, 85);line-height: 25px;padding: 5px 10px;background: #F9F9F9;border: 1px solid #EDEDED;
  }
  .detail{
    margin-top: 10px;
  }
}
</style>

<style scoped>
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    background-color: transparent;
    border: none;
    border-bottom: 1px solid #e3e3e3;
    color: #151515;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-header th{
    background-color: #F90;
    color: #000;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
    background: #f7f7f7!important;
  }
  .ivu-table-wrapper .ivu-table{
    background-color: #ffffff;
  }
  .ivu-table:after{
    width: 0;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    border-bottom: none;
  }
</style>
