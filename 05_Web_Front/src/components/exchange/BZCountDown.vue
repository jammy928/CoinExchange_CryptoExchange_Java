<template>
<div>
  <p>
    <div class="l-title" v-if="state==0">{{$t("exchange.publishcounttxt0")}}</div>
    <div class="l-title" v-if="state==1">{{$t("exchange.publishcounttxt1")}}</div>
    <div class="l-title" v-if="state==2">{{$t("exchange.publishcounttxt2")}}</div>
    <div class="l-title" v-if="state==3">{{$t("exchange.publishcounttxt3")}}</div>
  </p>
  <p style="margin-top: 15px;">
    <span class="num">{{day}}</span><span class="txt">{{$t("exchange.dateTimeDay")}}</span>
    <span class="num">{{hour}}</span><span class="txt">{{$t("exchange.dateTimeHour")}}</span>
    <span class="num">{{minutes}}</span><span class="txt">{{$t("exchange.dateTimeMinutes")}}</span>
    <span class="num">{{seconds}}</span><span class="txt">{{$t("exchange.dateTimeSeconds")}}</span>
  </p>
</div>
</template>

<script>
  export default{
    data () {
      return {
        currentT: 0,
        day: '00',
        hour: '00',
        minutes: '00',
        seconds: '00',
        timer : '',
        flag : false,
        state: 0,// 0:活动未开始   1：活动已开始但未结束  2：活动已结束但未清盘结束  3：活动已清盘结束
      }
    },
    mounted () {
      this.timer = setInterval(()=>{
        if(this.flag == true){
          clearInterval(this.timer);
        }else{
          this.timeDown();
        }
      },1000);
    },
    beforeDestroy() {
        if(this.timer) {
            clearInterval(this.timer);
        }
    },
    props : {
      countDownBgColor:{
        type : String
      },
      publishState:{
        type: Number
      },
      publishType : {
        type : String
      },
      currentTime: {
        type: Number
      },
      startTime : {
        type : String
      },
      endTime : {
        type : String
      },
      clearTime : {
        type : String
      },
      showPublishMask: {
        type: Function,
        default: null
      },
      hidePublishMask: {
        type: Function,
        default: null
      },
      hideCountDown: {
        type: Function,
        default: null
      }
    },
    methods : {
      timeDown () {
        var endTime = 0;

        const nowTime = parseInt(this.currentT + this.currentTime);
        const startT = parseInt(new Date(this.startTime).getTime()/1000);
        const endT = parseInt(new Date(this.endTime).getTime()/1000);
        const clearT = parseInt(new Date(this.clearTime).getTime()/1000);

        if(nowTime <= clearT){
          this.state = 2;
          endTime = clearT;
        }
        if(nowTime <= endT){
          this.state = 1;
          endTime = endT;
        }
        if(nowTime <= startT){
          this.state = 0;
          endTime = startT;
        }

        let leftTime = endTime - nowTime;
        this.day = this.formate(parseInt(leftTime/(24*60*60)));
        this.hour = this.formate(parseInt(leftTime/(60*60)%24));
        this.minutes = this.formate(parseInt(leftTime/60%60));
        this.seconds = this.formate(parseInt(leftTime%60));
        if(this.state == 0){
          // 活动未开始
          this.showPublishMask();
          this.$emit('update:countDownBgColor',"#003478")
          this.$emit('update:publishState', 0)
        }
        if(this.state == 1){
          // 活动开始中
          this.hidePublishMask();
          this.$emit('update:countDownBgColor',"#094802")
          this.$emit('update:publishState', 1)
        }
        if(this.state == 2){
          // 清盘中
          this.showPublishMask();
          this.$emit('update:countDownBgColor',"#5b0000")
          this.$emit('update:publishState', 2)
        }
        if(this.state == 2 && leftTime <= 0){
          // 清盘结束，正常交易
          this.hidePublishMask();
          this.hideCountDown();
          this.flag = true;
          this.state = 3;
          this.$emit('update:publishState', 3)
        }
        this.currentT = this.currentT + 1;
      },
      formate (time) {
        if(time>=10){
          return time
        }else{
          return `0${time}`
        }
      }
    }
  }
</script>

<style scoped>
.num{
  font-size:20px;
  background: linear-gradient(0deg, #df9000, #ffb100);
  padding: 2px 4px;
  border-radius: 2px;
  color: #000;
  text-shadow: 1px 1px 1px #908e8e;

}
.txt{
  font-size:12px;
}
</style>
