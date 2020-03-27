<template>
  <div class="index-wrap">
    <carousel-show></carousel-show>
    <div id="cont">
      <div id="adv">
        <h1 class="advH">{{$t('otc.index.title')}}</h1>
        <div class="advBtn">
          <button-group>
            <i-button v-for="(item,index) in btnList" @click="addClass(index)" :class="{'ivu-btn-primary':index==choseBtn,'ivu-btn-default':index!=choseBtn}">{{item.text}}</i-button>
          </button-group>
        </div>
        <div class="item1" v-show="isitem1">
          <Row type="flex" justify="space-around" class="code-row-bg adv_box">
            <Col span="6" v-for="(item,index) in cardListBuy0" class="card-item">
            <Card>
              <router-link :to="'/tradeInfo?tradeId='+item.advertiseId" class="tread-list trade-item">
                <div class="trade-body">
                  <div class="user-info">
                    <div class="user-face user-avatar-public">
                      <span class="user-avatar-in">{{item.memberName.replace(/^\s+|\s+$/g, "").slice(0, 1)}}</span>
                      <!-- <span class="online-status-box user-states" data-tuid="280000" data-tid="26896">
                                                                      <span class="circles"></span>
                                                                  </span> -->
                    </div>
                    <div class="user-name">
                      {{item.memberName}}
                    </div>
                    <!-- <div class="states merchant"></div> -->
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangetimes')}}：</span>
                    {{item.transactions}}
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangeprice')}}：</span>
                    <span style="font-weight: bold">
                      {{item.price}}
                    </span>
                    CNY
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangelimit')}}：</span>
                    {{item.minLimit}}-{{item.maxLimit}} CNY
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.paymode')}}：</span>{{item.payMode}}
                  </div>
                  <div class="tradeBtn">
                    <Button type="primary" icon="ios-cart-outline" size="large" long>{{$t('otc.index.buy')}}{{item.coin}}</Button>
                  </div>
                </div>
              </router-link>
            </Card>
            </Col>
          </Row>
        </div>
        <div class="item2" v-show="!isitem1">
          <Row type="flex" justify="space-around" class="code-row-bg adv_box">
            <Col span="6" v-for="(item,index) in cardListBuy1" class="card-item">
            <Card>
              <router-link :to="'/tradeInfo?tradeId='+item.advertiseId" class="tread-list trade-item">
                <div class="trade-body">
                  <div class="user-info">
                    <div class="user-face user-avatar-public">
                      <span class="user-avatar-in">{{item.memberName.replace(/^\s+|\s+$/g, "").slice(0, 1)}}</span>
                      <!-- <span class="online-status-box user-states" data-tuid="280000" data-tid="26896">
                                                                      <span class="circles"></span>
                                                                  </span> -->
                    </div>
                    <div class="user-name">
                      {{item.memberName}}
                    </div>
                    <!-- <div class="states merchant"></div> -->
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangetimes')}}：</span>
                    {{item.transactions}}
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangeprice')}}：</span>
                    <span style="font-weight: bold">
                      {{item.price}}
                    </span>
                    CNY
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.exchangelimit')}}：</span>
                    {{item.minLimit}}-{{item.maxLimit}} CNY
                  </div>
                  <div class="trade-info">
                    <span>{{$t('otc.index.paymode')}}：</span>{{item.payMode}}
                  </div>
                  <div class="tradeBtn">
                    <Button type="primary" icon="ios-color-wand-outline" size="large" long>{{$t('otc.index.sell')}}{{item.coin}}</Button>
                  </div>
                </div>
              </router-link>
            </Card>
            </Col>
          </Row>
        </div>
        <div class="morebox">
          <router-link to="/tradingcenter/coin1buy">{{$t('otc.index.viewmore')}}&gt;&gt;</router-link>
        </div>
        <!-- adv -->
        <div class="index-bot">
          <div class="ww">
            <Row type="flex" justify="space-around" class="code-row-bg">
              <Col span="5" class="bot_item">
              <img src="../assets/img/index_bot1.png" alt="">
              <h5>{{$t('otc.index.bot1')}}</h5>
              <p>{{$t('otc.index.bot1_tip')}}</p>
              </Col>
              <Col span="5" class="bot_item">
              <img src="../assets/img/index_bot2.png" alt="">
              <h5>{{$t('otc.index.bot2')}}</h5>
              <p>{{$t('otc.index.bot2_tip')}}</p>
              </Col>
              <Col span="5" class="bot_item">
              <img src="../assets/img/index_bot3.png" alt="">
              <h5>{{$t('otc.index.bot3')}}</h5>
              <p>{{$t('otc.index.bot3_tip')}}</p>
              </Col>
            </Row>
          </div>
        </div>
        <!-- 3box -->
      </div>
    </div>
  </div>
</template>
<script>
import carouselShow from '../components/carousel'
export default {
  components: {
    carouselShow
  },
  data() {
    return {
      choseBtn: 0,
      isitem1: true,
      btnList: [
        {
          text: this.$t('otc.index.ibuy')
        },
        {
          text: this.$t('otc.index.isell')
        },
      ],
      cardListBuy0: [],
      cardListBuy1: [],
    }
  },
  mounted() {

  },
  methods: {
    addClass(index) {
      this.choseBtn = index;
      if (index == 0) {
        this.isitem1 = true;
      } else {
        this.isitem1 = false;
      }
      this.getAd(index)
    },
    init() {
      this.$store.state.HeaderActiveName = '1';
      this.getAd(0)
      this.getAd(1)
    },
    getAd(index) {
      if (index == 0) {
        this.$http.post(this.host + '/api/advertise/excellent', { 'advertiseType': 0 }).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.cardListBuy0 = resp.data.slice(0, 4)
          } else if (resp.code == 4000) {
            this.$Message.success(this.$t('common.logintip'));
            this.$router.push('/login');
          } else {
            this.$Message.error(resp.message);
          }
        })
      } else if (index == 1) {
        this.$http.post(this.host + '/api/advertise/excellent', { 'advertiseType': 1 }).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.cardListBuy1 = resp.data.slice(0, 4)
          } else if (resp.code == 4000) {
            this.$Message.success(this.$t('common.logintip'));
            this.$router.push('/login');
          } else {
            this.$Message.error(resp.message);
          }
        })
      }

    }
  },
  created: function() {
    this.init();
  },
}
</script>
<style>
.morebox {
  text-align: center;
  padding: 50px 0;
}

.morebox a {
  line-height: 40px;
  font-size: 16px;
  color: #626262;
  text-decoration: underline;
}

.index-bot {
  background: #F9F9FB;
  padding: 80px 0;
}

.adv_box .ivu-card,
.adv_box .ivu-card-body {
  height: 405px;
}

.bot_item h5 {
  font-size: 20px;
  color: #0B0D1B;
  line-height: 50px;
  margin-top: 40px;
}

.bot_item p {
  font-size: 14px;
  color: #949494;
}

.tradeBtn {
  padding: 15px 0;
}

.trade-body {
  transform: translateY(-100px);
}

.adv_box .ivu-card-body {
  height: 405px;
}

.adv_box .user-avatar-public {
  background: #fff;
  border-radius: 50%;
  height: 120px;
  width: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 1px 5px 0 rgba(71, 78, 114, 0.24);
  position: relative;
}

.card-item:hover .user-avatar-public {
  box-shadow: 0 1px 5px 0 #F55F45;
}

.adv_box .user-avatar-public>.user-avatar-in {
  background: #131F31;
  border-radius: 50%;
  height: 110px;
  width: 110px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  margin-right: 0;
}

.card-item:hover .user-avatar-public>.user-avatar-in {
  background: #F55F45;
}

.user-avatar-public>.user-states {
  height: 14px;
  width: 14px;
  border-radius: 50%;
  /* background-color: white; */
  position: absolute;
  bottom: 3px;
  right: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-avatar-public>.user-states>.circles {
  height: 8px;
  width: 8px;
  border-radius: 50%;
  background-color: #00c481;
}

.user-avatar-public>.user-states.not-online>.circles {
  background-color: #b6bab9;
}

.trade-item .trade-body {
  padding: 0 10%;
  font-size: 16px;
}

.trade-item .trade-body .title {
  line-height: 70px;
  color: #fff;
  text-align: center;
}

.trade-item .trade-body .user-info {
  background: rgba(255, 255, 255, .16);
  display: flex;
  align-items: center;
  flex-direction: column;
  padding: 20px 0;
  margin-bottom: 30px;
  border-bottom: #e7e9ed dotted 1px;
}

.trade-item .trade-body .user-name {
  display: flex;
  flex-direction: column;
  font-size: 16px;
  padding: 35px 0 10px;
}

.trade-item .trade-body .online-status-box {
  color: #18b111;
  font-size: 12px;
}

.trade-item {
  color: #0D214D;
  text-align: left;
}

.trade-item .trade-body .user-name p:last-child {
  font-size: 12px;
}

.merchant {
  background: url(../assets/img/renzheng.png) no-repeat;
  background-size: 100% 100%;
  height: 17px;
  width: 67px;
  display: inline-block;
}

.trade-item .trade-body .trade-info {
  padding-bottom: 8px;
  font-size: 16px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trade-item .trade-body .trade-info span:first-child {
  color: #9094a7;
  font-size: 14px;
}

.trade-item .trade-btn {
  width: 80%;
  line-height: 45px;
  text-align: center;
  color: #00aeef;
  text-decoration: none;
  font-size: 16px;
  display: block;
  border: 1px solid #96e1fb;
  margin: 15px 10% 30px 10%;
}

.trade-item:hover .trade-btn {
  color: #fff;
  background: #00b5f6;
}

.trade-item .trade-btn .icon {
  width: 16px;
  height: 17px;
  /* background: url("../../images/index/comm_icon.png") no-repeat; */
  background: black;
  display: inline-block;
  vertical-align: middle;
}

.trade-item .trade-btn .icon.buy {
  background-position: 0 -30px;
}

.trade-item:hover .trade-btn .icon.buy {
  background-position: 0 0;
}

.trade-item .trade-btn .icon.sell {
  background-position: -27px -30px;
}

.trade-item:hover .trade-btn .icon.sell {
  background-position: -27px 0;
}

.trade-bottom a {
  color: #444f71;
  font-size: 14px;
  text-decoration: underline;
  padding: 10px 0;
  display: inline-block;
}

#cont {}

.advBtn {
  text-align: center;
  padding: 0 0 20px 0;
}

.advBtn button {
  padding: 10px 80px;
  font-size: 14px;
}

#adv {
  padding: 50px 0;
}

h1.advH {
  text-align: center;
  font-size: 24px;
  color: #0D214D;
  font-weight: normal;
  line-height: 80px;
}

.adv_box {
  width: 85%;
  margin: 0 auto;
  padding-top: 100px;
}

.adv_box .ivu-col {
  padding: 0 20px;
}
</style>


