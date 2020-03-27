<template>
  <div class="activity">
    <img class="bannerimg banner-pc" src="../../assets/images/activity-bg.jpg">
    <img class="bannerimg banner-mobile" src="../../assets/images/activity_mobile.jpg">
    <div class="activity_container">
      <h1>{{$t("header.labdetail")}}</h1>
      <p style="letter-spacing: 1px;">{{$t("activity.headertitledesc")}}</p>
      <div class="main">
          <Tabs :animated="false" style="width:100%;" @on-click="tabChange">
            <TabPane :label="$t('activity.all')" name="all">
              <div class="activity-container">
                <div class="tips-line" v-if="activityList.all.loaded && activityList.all.total == 0"><img src="../../assets/images/emptydata.png"></img></div>
                <div class="tips-line" v-if="!activityList.all.loaded"><Spin size="large"></Spin></div>
                <div class="activity-item" v-for="(item, index) in activityList.all.items" :key="index">
                  <div class="activity-type" v-if="item.type==1">{{$t('activity.activitytype1')}}</div>
                  <div class="activity-type" v-if="item.type==2">{{$t('activity.activitytype2')}}</div>
                  <div class="activity-type" v-if="item.type==3">{{$t('activity.activitytype3')}}</div>
                  <div class="activity-type" v-if="item.type==4">{{$t('activity.activitytype4')}}</div>
                  <Row>
                      <Col :xs="24" :sm="24" :md="4" :lg="4"><img :src="item.smallImageUrl"></img></Col>
                      <Col :xs="24" :sm="24" :md="20" :lg="20">
                        <div class="title">
                          <span>{{item.title}}</span>
                          <div v-if="item.step==0" class="step0">{{$t('activity.prepare')}}</div>
                          <div v-if="item.step==1" class="step1">{{$t('activity.ongoing')}}</div>
                          <div v-if="item.step==2" class="step2">{{$t('activity.tokendistribute')}}</div>
                          <div v-if="item.step==3" class="step3">{{$t('activity.completed')}}</div>
                        </div>
                        <div style="width: 100%;padding-top: 10px;">
                          <Row>
                              <Col :xs="24" :sm="24" :md="10" :lg="10">
                                <div class="progress-text" style="margin-bottom: -3px;">
                                  <span class="gray">{{$t('activity.progress')}}</span>
                                  <span class="gray">{{$t('activity.totalsupply')}}</span>
                                </div>
                                <Progress :percent="item.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                                <div class="progress-text">
                                  <span>{{item.progress | fixedScale(2)}}%</span>
                                  <span>{{item.totalSupply | fixedScale(item.amountScale)}} {{item.unit}}</span>
                                </div>
                              </Col>
                              <Col :xs="24" :sm="24" :md="14" :lg="14">
                                <p class="progress-time">{{$t('activity.starttime')}}：{{item.startTime | dateFormat}}</p>
                                <p class="progress-time">{{$t('activity.endtime')}}：{{item.endTime | dateFormat}}</p>
                              </Col>
                          </Row>
                          <Row class="bottom-panel">
                              <Col span="24">
                                <div class="bottom">
                                    <span><Icon type="md-information-circle" /> {{item.detail}}</span>
                                    <Button v-if="item.step==1" type="warning" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                </div>
                                <div class="bottom-mobile">
                                    <p><span><Icon type="md-information-circle" /> {{item.detail}}</span></p>
                                    <Button v-if="item.step==1" long type="warning" :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" long :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                </div>
                              </Col>
                          </Row>
                        </div>
                      </Col>
                  </Row>
                </div>
              </div>
              <div class="page" v-if="activityList.all.total > 0">
                <Page :total="activityList.all.total" :pageSize="activityList.all.pageSize" :current="activityList.all.pageNo" @on-change="pageChange"></Page>
              </div>
            </TabPane>
            <TabPane :label="$t('activity.prepare')" name="step0">
              <div class="activity-container">
                <div class="tips-line" v-if="activityList.step0.loaded && activityList.step0.total == 0"><img src="../../assets/images/emptydata.png"></img></div>
                <div class="tips-line" v-if="!activityList.step0.loaded"><Spin size="large"></Spin></div>
                <div class="activity-item" v-for="(item, index) in activityList.step0.items" :key="index">
                  <div class="activity-type" v-if="item.type==1">{{$t('activity.activitytype1')}}</div>
                  <div class="activity-type" v-if="item.type==2">{{$t('activity.activitytype2')}}</div>
                  <div class="activity-type" v-if="item.type==3">{{$t('activity.activitytype3')}}</div>
                  <div class="activity-type" v-if="item.type==4">{{$t('activity.activitytype4')}}</div>
                  <Row>
                      <Col :xs="24" :sm="24" :md="4" :lg="4"><img :src="item.smallImageUrl"></img></Col>
                      <Col :xs="24" :sm="24" :md="20" :lg="20">
                        <div class="title">
                          <span>{{item.title}}</span>
                          <div v-if="item.step==0" class="step0">{{$t('activity.prepare')}}</div>
                          <div v-if="item.step==1" class="step1">{{$t('activity.ongoing')}}</div>
                          <div v-if="item.step==2" class="step2">{{$t('activity.tokendistribute')}}</div>
                          <div v-if="item.step==3" class="step3">{{$t('activity.completed')}}</div>
                        </div>
                        <div style="width: 100%;padding-top: 10px;">
                          <Row>
                              <Col :xs="24" :sm="24" :md="10" :lg="10">
                                <div class="progress-text" style="margin-bottom: -3px;">
                                  <span class="gray">{{$t('activity.progress')}}</span>
                                  <span class="gray">{{$t('activity.totalsupply')}}</span>
                                </div>
                                <Progress :percent="item.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                                <div class="progress-text">
                                  <span>{{item.progress | fixedScale(2)}}%</span>
                                  <span>{{item.totalSupply | fixedScale(item.amountScale)}} {{item.unit}}</span>
                                </div>
                              </Col>
                              <Col :xs="24" :sm="24" :md="14" :lg="14">
                                <p class="progress-time">{{$t('activity.starttime')}}：{{item.startTime | dateFormat}}</p>
                                <p class="progress-time">{{$t('activity.endtime')}}：{{item.endTime | dateFormat}}</p>
                              </Col>
                          </Row>
                          <Row class="bottom-panel">
                              <Col span="24">
                                <div class="bottom">
                                    <span><Icon type="md-information-circle" /> {{item.detail}}</span>
                                    <Button v-if="item.step==1" type="warning" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                </div>
                                <div class="bottom-mobile">
                                    <p><span><Icon type="md-information-circle" /> {{item.detail}}</span></p>
                                    <Button v-if="item.step==1" long type="warning" :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" long :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                </div>
                              </Col>
                          </Row>
                        </div>
                      </Col>
                  </Row>
                </div>
              </div>
              <div class="page" v-if="activityList.step0.total > 0">
                <Page :total="activityList.step0.total" :pageSize="activityList.step0.pageSize" :current="activityList.step0.pageNo" @on-change="pageChange"></Page>
              </div>
            </TabPane>
            <TabPane :label="$t('activity.ongoing')" name="step1">
              <div class="activity-container">
                <div class="tips-line" v-if="activityList.step1.loaded && activityList.step1.total == 0"><img src="../../assets/images/emptydata.png"></img></div>
                <div class="tips-line" v-if="!activityList.step1.loaded"><Spin size="large"></Spin></div>
                <div class="activity-item" v-for="(item, index) in activityList.step1.items" :key="index">
                  <div class="activity-type" v-if="item.type==1">{{$t('activity.activitytype1')}}</div>
                  <div class="activity-type" v-if="item.type==2">{{$t('activity.activitytype2')}}</div>
                  <div class="activity-type" v-if="item.type==3">{{$t('activity.activitytype3')}}</div>
                  <div class="activity-type" v-if="item.type==4">{{$t('activity.activitytype4')}}</div>
                  <Row>
                      <Col :xs="24" :sm="24" :md="4" :lg="4"><img :src="item.smallImageUrl"></img></Col>
                      <Col :xs="24" :sm="24" :md="20" :lg="20">
                        <div class="title">
                          <span>{{item.title}}</span>
                          <div v-if="item.step==0" class="step0">{{$t('activity.prepare')}}</div>
                          <div v-if="item.step==1" class="step1">{{$t('activity.ongoing')}}</div>
                          <div v-if="item.step==2" class="step2">{{$t('activity.tokendistribute')}}</div>
                          <div v-if="item.step==3" class="step3">{{$t('activity.completed')}}</div>
                        </div>
                        <div style="width: 100%;padding-top: 10px;">
                          <Row>
                              <Col :xs="24" :sm="24" :md="10" :lg="10">
                                <div class="progress-text" style="margin-bottom: -3px;">
                                  <span class="gray">{{$t('activity.progress')}}</span>
                                  <span class="gray">{{$t('activity.totalsupply')}}</span>
                                </div>
                                <Progress :percent="item.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                                <div class="progress-text">
                                  <span>{{item.progress | fixedScale(2)}}%</span>
                                  <span>{{item.totalSupply | fixedScale(item.amountScale)}} {{item.unit}}</span>
                                </div>
                              </Col>
                              <Col :xs="24" :sm="24" :md="14" :lg="14">
                                <p class="progress-time">{{$t('activity.starttime')}}：{{item.startTime | dateFormat}}</p>
                                <p class="progress-time">{{$t('activity.endtime')}}：{{item.endTime | dateFormat}}</p>
                              </Col>
                          </Row>
                          <Row class="bottom-panel">
                              <Col span="24">
                                <div class="bottom">
                                    <span><Icon type="md-information-circle" /> {{item.detail}}</span>
                                    <Button v-if="item.step==1" type="warning" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                </div>
                                <div class="bottom-mobile">
                                    <p><span><Icon type="md-information-circle" /> {{item.detail}}</span></p>
                                    <Button v-if="item.step==1" long type="warning" :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" long :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                </div>
                              </Col>
                          </Row>
                        </div>
                      </Col>
                  </Row>
                </div>
              </div>
              <div class="page" v-if="activityList.step1.total > 0">
                <Page :total="activityList.step1.total" :pageSize="activityList.step1.pageSize" :current="activityList.step1.pageNo" @on-change="pageChange"></Page>
              </div>
            </TabPane>
            <TabPane :label="$t('activity.distributing')" name="step2">
              <div class="activity-container">
                <div class="tips-line" v-if="activityList.step2.loaded && activityList.step2.total == 0"><img src="../../assets/images/emptydata.png"></img></div>
                <div class="tips-line" v-if="!activityList.step2.loaded"><Spin size="large"></Spin></div>
                <div class="activity-item" v-for="(item, index) in activityList.step2.items" :key="index">
                  <div class="activity-type" v-if="item.type==1">{{$t('activity.activitytype1')}}</div>
                  <div class="activity-type" v-if="item.type==2">{{$t('activity.activitytype2')}}</div>
                  <div class="activity-type" v-if="item.type==3">{{$t('activity.activitytype3')}}</div>
                  <div class="activity-type" v-if="item.type==4">{{$t('activity.activitytype4')}}</div>
                  <Row>
                      <Col :xs="24" :sm="24" :md="4" :lg="4"><img :src="item.smallImageUrl"></img></Col>
                      <Col :xs="24" :sm="24" :md="20" :lg="20">
                        <div class="title">
                          <span>{{item.title}}</span>
                          <div v-if="item.step==0" class="step0">{{$t('activity.prepare')}}</div>
                          <div v-if="item.step==1" class="step1">{{$t('activity.ongoing')}}</div>
                          <div v-if="item.step==2" class="step2">{{$t('activity.tokendistribute')}}</div>
                          <div v-if="item.step==3" class="step3">{{$t('activity.completed')}}</div>
                        </div>
                        <div style="width: 100%;padding-top: 10px;">
                          <Row>
                              <Col :xs="24" :sm="24" :md="10" :lg="10">
                                <div class="progress-text" style="margin-bottom: -3px;">
                                  <span class="gray">{{$t('activity.progress')}}</span>
                                  <span class="gray">{{$t('activity.totalsupply')}}</span>
                                </div>
                                <Progress :percent="item.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                                <div class="progress-text">
                                  <span>{{item.progress | fixedScale(2)}}%</span>
                                  <span>{{item.totalSupply | fixedScale(item.amountScale)}} {{item.unit}}</span>
                                </div>
                              </Col>
                              <Col :xs="24" :sm="24" :md="14" :lg="14">
                                <p class="progress-time">{{$t('activity.starttime')}}：{{item.startTime | dateFormat}}</p>
                                <p class="progress-time">{{$t('activity.endtime')}}：{{item.endTime | dateFormat}}</p>
                              </Col>
                          </Row>
                          <Row class="bottom-panel">
                              <Col span="24">
                                <div class="bottom">
                                    <span><Icon type="md-information-circle" /> {{item.detail}}</span>
                                    <Button v-if="item.step==1" type="warning" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                </div>
                                <div class="bottom-mobile">
                                    <p><span><Icon type="md-information-circle" /> {{item.detail}}</span></p>
                                    <Button v-if="item.step==1" long type="warning" :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" long :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                </div>
                              </Col>
                          </Row>
                        </div>
                      </Col>
                  </Row>
                </div>
              </div>
              <div class="page" v-if="activityList.step2.total > 0">
                <Page :total="activityList.step2.total" :pageSize="activityList.step2.pageSize" :current="activityList.step2.pageNo" @on-change="pageChange"></Page>
              </div>
            </TabPane>
            <TabPane :label="$t('activity.completed')" name="step3">
              <div class="activity-container">
                <div class="tips-line" v-if="activityList.step3.loaded && activityList.step3.total == 0"><img src="../../assets/images/emptydata.png"></img></div>
                <div class="tips-line" v-if="!activityList.step3.loaded"><Spin size="large"></Spin></div>
                <div class="activity-item" v-for="(item, index) in activityList.step3.items" :key="index">
                  <div class="activity-type" v-if="item.type==1">{{$t('activity.activitytype1')}}</div>
                  <div class="activity-type" v-if="item.type==2">{{$t('activity.activitytype2')}}</div>
                  <div class="activity-type" v-if="item.type==3">{{$t('activity.activitytype3')}}</div>
                  <div class="activity-type" v-if="item.type==4">{{$t('activity.activitytype4')}}</div>
                  <Row>
                      <Col :xs="24" :sm="24" :md="4" :lg="4"><img :src="item.smallImageUrl"></img></Col>
                      <Col :xs="24" :sm="24" :md="20" :lg="20">
                        <div class="title">
                          <span>{{item.title}}</span>
                          <div v-if="item.step==0" class="step0">{{$t('activity.prepare')}}</div>
                          <div v-if="item.step==1" class="step1">{{$t('activity.ongoing')}}</div>
                          <div v-if="item.step==2" class="step2">{{$t('activity.tokendistribute')}}</div>
                          <div v-if="item.step==3" class="step3">{{$t('activity.completed')}}</div>
                        </div>
                        <div style="width: 100%;padding-top: 10px;">
                          <Row>
                              <Col :xs="24" :sm="24" :md="10" :lg="10">
                                <div class="progress-text" style="margin-bottom: -3px;">
                                  <span class="gray">{{$t('activity.progress')}}</span>
                                  <span class="gray">{{$t('activity.totalsupply')}}</span>
                                </div>
                                <Progress :percent="item.progress" status="active" style="width: 100%;" hide-info :stroke-width="5"/>
                                <div class="progress-text">
                                  <span>{{item.progress | fixedScale(2)}}%</span>
                                  <span>{{item.totalSupply | fixedScale(item.amountScale)}} {{item.unit}}</span>
                                </div>
                              </Col>
                              <Col :xs="24" :sm="24" :md="14" :lg="14">
                                <p class="progress-time">{{$t('activity.starttime')}}：{{item.startTime | dateFormat}}</p>
                                <p class="progress-time">{{$t('activity.endtime')}}：{{item.endTime | dateFormat}}</p>
                              </Col>
                          </Row>
                          <Row class="bottom-panel">
                              <Col span="24">
                                <div class="bottom">
                                    <span><Icon type="md-information-circle" /> {{item.detail}}</span>
                                    <Button v-if="item.step==1" type="warning" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" :to="'/lab/detail/'+ item.id" target="_blank">{{$t('activity.viewdetail')}}</Button>
                                </div>
                                <div class="bottom-mobile">
                                    <p><span><Icon type="md-information-circle" /> {{item.detail}}</span></p>
                                    <Button v-if="item.step==1" long type="warning" :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                    <Button v-else type="primary" long :to="'/lab/detail/'+ item.id">{{$t('activity.viewdetail')}}</Button>
                                </div>
                              </Col>
                          </Row>
                        </div>
                      </Col>
                  </Row>
                </div>
              </div>
              <div class="page" v-if="activityList.step3.total > 0">
                <Page :total="activityList.step3.total" :pageSize="activityList.step3.pageSize" :current="activityList.step3.pageNo" @on-change="pageChange"></Page>
              </div>
            </TabPane>
          </Tabs>
      </div>
    </div>
    <div class="app_bottom">
      <div class="left_logo">
        <img style="float:left;" src="../../assets/images/applogo.png"></img>
        <div style="float:left;height: 40px;line-height:40px;color:#000;">{{$t("cms.downloadslogan")}}</div>
      </div>
      <div class="right_btn_wrap"><router-link to="/app" class="right_btn">{{$t("cms.download")}}</router-link></div>
    </div>
  </div>
</template>

<script>
var moment = require("moment");
export default {

  data() {
    return {
      currentCate: "all",
      activityList:{
        all:{
          loaded: false,
          pageSize: 10,
          pageNo: 1,
          total: 0,
          items: []
        },
        step0:{
          loaded: false,
          pageSize: 10,
          pageNo: 1,
          total: 0,
          items: []
        },
        step1:{
          loaded: false,
          pageSize: 10,
          pageNo: 1,
          total: 0,
          items: []
        },
        step2:{
          loaded: false,
          pageSize: 10,
          pageNo: 1,
          total: 0,
          items: []
        },
        step3:{
          loaded: false,
          pageSize: 10,
          pageNo: 1,
          total: 0,
          items: []
        }
      }
    };
  },
  created: function() {
    this.init();
    this.getData();
  },
  filters:{
    dateFormat: function(tick) {
      return moment(tick).format("YYYY-MM-DD HH:mm:ss");
    },
    fixedScale: function(value, scale) {
      return value.toFixed(scale);
    }
  },
  computed: {
    lang() {
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
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-lab");
    },
    detail(aId){
      this.$router.push("/lab/detail/" + aId);
    },
    pageChange(page){
      if(this.currentCate == "all"){
        this.activityList.all.pageNo = page;
        this.getData(-1);
      }
      if(this.currentCate == "step0"){
        this.activityList.step0.pageNo = page;
        this.getData(0);
      }
      if(this.currentCate == "step1"){
        this.activityList.step1.pageNo = page;
        this.getData(1);
      }
      if(this.currentCate == "step2"){
        this.activityList.step2.pageNo = page;
        this.getData(2);
      }
      if(this.currentCate == "step3"){
        this.activityList.step3.pageNo = page;
        this.getData(3);
      }
    },
    tabChange(name) {
      this.currentCate = name;
      if(name == "all" && !this.activityList.all.loaded){
        this.getData(-1);
      }
      if(name == "step0" && !this.activityList.step0.loaded){
        this.getData(0);
      }
      if(name == "step1" && !this.activityList.step1.loaded){
        this.getData(1);
      }
      if(name == "step2" && !this.activityList.step2.loaded){
        this.getData(2);
      }
      if(name == "step3" && !this.activityList.step3.loaded){
        this.getData(3);
      }
    },
    getData(step = -1) {
      let param = {};
      param["step"] = step;

      if(step == -1) param["pageNo"] = this.activityList.all.pageNo;
      if(step == 0) param["pageNo"] = this.activityList.step0.pageNo;
      if(step == 1) param["pageNo"] = this.activityList.step1.pageNo;
      if(step == 2) param["pageNo"] = this.activityList.step2.pageNo;
      if(step == 3) param["pageNo"] = this.activityList.step3.pageNo;

      param["pageSize"] = 10;
      this.$http.post(this.host + "/uc/activity/page-query", param).then(res => {
        if (res.status == 200 && res.body.code == 0) {
          let aList = res.body.data.content;
          // 持仓瓜分类型进度处理（未开始：0，进行中：50，派发中：75%，已结束：100
          for (var i = 0; i < aList.length; i++) {
            if(aList[i].type ==3){
              if(aList[i].step == 1){
                    aList[i].progress = 50;
                }else if(aList[i].step == 2){
                    aList[i].progress = 75;
                }else if(aList[i].step == 3){
                    aList[i].progress = 100;
                }else{
                    aList[i].progress = 0;
                }
            }
          }
          if(step == -1){
            this.activityList.all.loaded = true;
            this.activityList.all.items = aList;
            this.activityList.all.total = res.body.data.totalElements;
          }else if(step == 0){
            this.activityList.step0.loaded = true;
            this.activityList.step0.items = aList;
            this.activityList.step0.total = res.body.data.totalElements;
          }else if(step == 1){
            this.activityList.step1.loaded = true;
            this.activityList.step1.items = aList;
            this.activityList.step1.total = res.body.data.totalElements;
          }else if(step == 2){
            this.activityList.step2.loaded = true;
            this.activityList.step2.items = aList;
            this.activityList.step2.total = res.body.data.totalElements;
          }else if(step == 3){
            this.activityList.step3.loaded = true;
            this.activityList.step3.items = aList;
            this.activityList.step3.total = res.body.data.totalElements;
          }
        } else {
          this.$Message.error(res.body.message);
        }
      });
    }
  }
};
</script>

<style>
    .activity .ivu-tabs-bar{
        border-bottom: 1px solid #dcdee2;
    }
    .activity .ivu-tabs-nav .ivu-tabs-tab:hover{
        color: #f0a70a;
    }
    .activity .ivu-tabs-nav .ivu-tabs-tab:hover, .activity .ivu-tabs-nav .ivu-tabs-tab-active{
        color: #f0a70a;
    }
    .activity .ivu-tabs-ink-bar{
        background-color: #f0a70a;
    }
    .app_bottom{
      display: none;
      z-index: 999;
      position: fixed;
      bottom: 0;
      left: 0;
      width: 100%;
      height: 40px;
      background: rgba(242,246,250,1) !important;
    }
    .app_bottom .left_logo img{
      height: 30px;margin-top:5px;border-radius: 5px;margin-left: 5px;margin-right: 5px;
    }
    .app_bottom .right_btn_wrap{
      float: right;height: 40px;line-height: 40px;margin-right: 5px;
    }
    .app_bottom .right_btn{
      color: #FFF;
      border-radius: 3px;
      padding: 0px 10px;
      line-height: 26px;
      height: 26px;
      display: block;
      margin-top: 7px;
      background: linear-gradient(200deg, #ff9900, #ffbe2b, #ffa500);
    }
    @media screen and (max-width:768px){
      .activity{
          padding-top: 45px!important;
      }
      .activity_container {
        padding: 0 2%!important;
      }
      .activity .main {
        margin-top: 70px !important;
      }
      .progress-time{
        text-align: left!important;
        padding-right: 0px!important;
        margin-top: 10px!important;
      }
      .activity-item .title{
        text-align: left!important;
      }
      .activity-item .title div{
        display: none!important;
      }
      .activity-item .activity-type{
        display: none!important;
      }
      .banner-pc{
        display: none!important;
      }
      .banner-mobile{
        display: block!important;
      }
      .activity_container > h1{
        font-size: 20px!important;
        margin-top: -170px!important;
      }
      .activity-item img {
          width: 100px!important;
          height: 100px!important;
      }
      .activity-item .title span{
        font-size:18px!important;
      }
      .app_bottom{
        display: block!important;
      }
      .bottom-panel .bottom{
        display: none!important;
      }
      .bottom-panel .bottom-mobile{
        display: block!important;
      }
      .bottom-panel .bottom-mobile p{
        margin: 10px 0 10px 0;
      }
      .bottom-panel .bottom-mobile p span{
          font-size: 12px;
          color: #a7a7a7;
          margin-top:15px;
      }
    }
    .banner-pc{
      display: block;
    }
    .banner-mobile{
      display: none;
    }
</style>
<style lang="scss" scoped>
  .activity {
    background: rgba(242,246,250,1) !important;
    height: 100%;
    background-size: cover;
    position: relative;
    overflow: hidden;
    padding-bottom: 50px;
    padding-top: 60px;
    color: #fff;
  }
  .activity .bannerimg {
    width: 100%;
  }
  .activity_container {
    padding: 0 12%;
    text-align: center;
    height: 100%;
    min-height: 600px;
    > h1 {
      margin-top: -200px;
      font-size: 32px;
      line-height: 1;
      padding: 50px 0 20px 0;
      letter-spacing: 3px;
    }
  }
  .activity .main {
    margin-top: 120px;
    display: flex;
    justify-content: space-between;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .activity-container{
    min-height: 200px;
  }
  .tips-line{
    height: 100px;
    line-height: 100px;
    text-align: center;
    position: relative;
    display: inline-block;
    img{
      width: 100px;
      margin-top: 50px;
    }
    .ivu-spin{
      margin-top: 50px;
    }
  }
  .activity-item{
    position:relative;
    overflow: hidden;
    padding: 15px 20px;
    margin-top: 25px;
    margin-bottom: 25px;
    width: 98%;
    margin-left: 1%;
    min-height: 20px;
    background:#FFF;
    border-radius: 5px;
    transition: transform 0.2s ease;
    &:hover{
      box-shadow: 0 0 25px #DDD;
      transform: scale(1.01,1.01);
    }
    .activity-type{
      position: absolute;
      width: 140px;
      height: 25px;
      line-height: 25px;
      background-color: #000;
      color: white;
      text-align: center;
      transform: rotate(-40deg);
      top: 23px;
      left: -30px;
      z-index: 99;
      box-shadow: 1px 1px 4px #000;
      z-index: 99;
    }
    .title{
      width: 100%;padding-top: 10px;display:flex;flex-direction: row;
      span{
        font-size: 22px;color:rgba(49,54,62,1) !important;
      }
      div{
        height: 30px;
        line-height: 30px;
        padding: 0 15px 0 25px;
        border-top-left-radius: 15px;
        border-bottom-left-radius: 15px;
        border-top-right-radius: 3px;
        border-bottom-right-radius: 3px;
        position: relative;
        &:before{
          content: "●";
          position:absolute;
          top: -1px;
          left: 5px;
        }
      }
      div.step0{
        margin-left: 15px;
        color: #FFF;
        border: 1px solid #FFF;
        background: #5bacff;
        background-image: repeating-linear-gradient(60deg, hsla(0,0%,100%,.1), hsla(0,0%,100%,.1) 10px, transparent 0, transparent 20px);
      }
      div.step1{
        margin-left: 15px;
        color: #ffffff;
        border: 1px solid #ffffff;
        background: #19be6b;
        background-image: repeating-linear-gradient(60deg, hsla(0,0%,100%,.1), hsla(0,0%,100%,.1) 10px, transparent 0, transparent 20px);
      }
      div.step2{
        margin-left: 15px;
        color: #FFFFFF;
        border: 1px solid #ffffff;
        background: #F90;
        background-image: repeating-linear-gradient(60deg, hsla(0,0%,100%,.1), hsla(0,0%,100%,.1) 10px, transparent 0, transparent 20px);
      }
      div.step3{
        margin-left: 15px;
        color: #ffffff;
        border: 1px solid #ffffff;
        background: #828282;
        background-image: repeating-linear-gradient(60deg, hsla(0,0%,100%,.1), hsla(0,0%,100%,.1) 10px, transparent 0, transparent 20px);
      }
    }
    img{
      width: 160px;
      height: 160px;
    }
  }
  .progress-text{
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    span{
      font-size: 12px;
    }
  }
  .bottom-panel{
      border-top: 1px solid rgb(237, 237, 237);margin-top: 15px;
      .bottom-mobile{
        display: none;
      }
      .bottom{
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        span{
          font-size: 12px;
          color: #a7a7a7;
          margin-top:15px;
        }
        button, a{
          margin-top: 11px;
        }
        a.ivu-btn-primary{
          background:#0095ff;
        }
        a.ivu-btn-primary:hover{
          background: #2ba7ff;
        }
      }
  }
  .progress-time{
    font-size: 13px;
    letter-spacing: 1px;
    text-align: right;
    padding-right: 80px;
  }
  .right{
    float: right;
  }
  .left{
    float: left;
  }
  .gray{
    color: #a7a7a7;
  }
</style>
