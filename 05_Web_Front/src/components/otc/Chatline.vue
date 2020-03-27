<template>
    <div>
        <div class="chat-line">
            <div class="scroll-chat" id="scrollChat">
                <div class="chat-container-box" id="sysbox">
                    <div class="system-box">
                        <p class="msg-content" v-show="msg.status==1">{{$t('otc.chatline.status_1')}}</p>
                        <p class="msg-content" v-show="msg.status==2">{{$t('otc.chatline.status_2')}}</p>
                        <p class="msg-content" v-show="msg.status==3">{{$t('otc.chatline.status_3')}}</p>
                        <p class="msg-content" v-show="msg.status==4">{{$t('otc.chatline.status_4')}}</p>
                        <p class="msg-content" v-show="msg.status==0">{{$t('otc.chatline.status_5')}}</p>
                    </div>
                </div>
                <h5 class="more" v-show="currentPage<totalPage||currentPage==totalPage">
                    <Icon type="ios-clock-outline" class="clock"></Icon>
                    <span @click="getBefore">{{$t('otc.chatline.loadmore')}}</span>
                </h5>
                <template v-for="item in msgLists">
                    <div class="chat-container-box" v-show="item.uidFrom!=msg.myId">
                        <div class="user-chat-box">
                            <div class="user-avatar-box">
                                <div class="avatar-box">
                                    <div class="user-face user-avatar-public">
                                        <span class="user-avatar-in" v-if="typeof item.fromAvatar == 'undefined' || item.fromAvatar == null || item.fromAvatar==''">{{msgnameS}}</span>
                                        <img v-else style="width: 42px;height:42px" :src="item.fromAvatar"/>
                                    </div>
                                </div>
                            </div>
                            <div class="user-content-box">
                                <p class="user-name">{{msg.otherSide}}</p>
                                <div class="chat-info">
                                    <div class="user-desc">
                                        <p class="icon"></p>
                                        <em class="input-in">{{item.content}}</em>
                                    </div>
                                    <span class="times">{{item.sendTimeStr}}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="chat-container-box" v-show="item.uidFrom==msg.myId">
                        <div class="user-chat-box self-chat-box">
                            <div class="user-content-box">
                                <p class="user-name">{{user.username}}</p>
                                <div class="chat-info">
                                    <div class="user-desc self-desc">
                                        <p class="icon"></p>
                                        <em class="input-in">{{item.content}}</em>
                                    </div>
                                    <span class="times">{{item.timeNow||item.sendTimeStr}}</span>
                                </div>
                            </div>
                            <div class="user-avatar-box">
                                <div class="avatar-box">
                                    <div class="user-face user-avatar-public">
                                        <span class="user-avatar-in" v-if="typeof item.fromAvatar=='undefined' || item.fromAvatar==null || item.fromAvatar==''">{{usernameS}}</span>
                                       <img v-else style="width: 42px;height:42px" :src="item.fromAvatar" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>

            </div>
            <!---->
            <h5 class="spe_show">
                <i class="iconfont icon-warning"></i>
                {{$t('otc.chatline.warning')}}
            </h5>
            <!---->
          <div class="audio-wrap">
            <audio id="noticeMusic" :src="audioSrc">您的浏览器不支持 audio 标签。</audio>
          </div>
            <div class="send-msg-box">
                <div class="img-btn" style="cursor: default;background-color:#192330;">
                    <!-- <Icon type="image" class="img-icons"></Icon>
                                            <input title="上传图片" type="file" accept="image/jpg,image/jpeg,image/png" class="input-file"> -->
                </div>
                <input v-model="mytext" @keyup.enter="sendName" autocomplete="off" type="text" :placeholder="$t('otc.chatline.contenttip')" style=" background-attachment: scroll; background-size: 16px 18px; background-position: 98% 50%;">
                <button class="send-btn" @click="sendName">
                    <Icon type="md-paper-plane" size="28" color="#f0ac19"/>
                </button>
              <div class="msg-notice">
                <!--<Checkbox @on-change="handleCheckClick" v-model="fOpenNotice">开启windows消息提醒,不担心错过任何消息</Checkbox>-->
                <Checkbox @on-change="handleNoticeClick" v-model="fOpenNotice">开启桌面消息提醒</Checkbox>
                <Checkbox @on-change="handleAudioClick" v-model="fOpenAudio">开启声音消息提醒</Checkbox>
              </div>
            </div>

        </div>
    </div>
</template>
<script>
var Stomp = require('stompjs');
var SockJS = require('sockjs-client');
var stompClient = null;
export default {
    props: ['msg'],
    data() {
        return {
            audioSrc:'/assets/audio/notice.wav',
            fOpenAudio:true,
            fOpenNotice:false,
            currentPage: 1,
            totalPage: 1,
            showMore: true,
            mytext: '',
            othermsg: '11',
            mymsg: '22',
            msgLists: [],
            otherList: [],
            myList: [],
            myitem: {},
            otheritem: {},
            timeNow: '',
            systext: '',
        }
    },
    computed: {
        msgnameS: function() {
            return (this.msg.otherSide + '').slice(0, 1)
        },
        user: function() {
            return JSON.parse(localStorage.getItem("MEMBER"))
        },
        usernameS: function() {
            return (this.user.username + '').slice(0, 1)
        },
        orderId: function() {
            return this.$route.query.tradeId
        },

    },
    watch: {
        'processData': 'scrollToBottom',
    },
    methods: {
        //让浏览器滚动条保持在最低部
        scrollToBottom: function() {
            this.$nextTick(() => {
                var div = document.getElementById('scrollChat')
                div.scrollTop = div.scrollHeight
            })
        },
        playAudio:function () {
            var audio = document.getElementById('noticeMusic');
            if(audio!==null) audio.play();
        },
        handleNoticeClick:function () {
            if (this.fOpenNotice) {
              if (window.Notification) {
                if (Notification.permission == "default") {
                  this.$Message.info('请点击允许进行开启!');
                  Notification.requestPermission().then(function(result) {
                    // result可能是是granted, denied, 或default.
                    if(result == "denied") this.$Message.info('您已屏蔽消息提醒，如需开通，请查看帮助!');
                  });
                }else if(Notification.permission == "denied") {
                  this.$Message.info('您已屏蔽消息提醒，如需开通，请查看帮助!');
                }else {
                  this.$Message.info("您已开启桌面消息提醒!");
                }
              } else {
                this.$Message.info('您的浏览器不支持该功能');
              }
            }else {
              this.$Message.info("您已关闭桌面消息提醒!");
            }
        },
        handleAudioClick:function () {
          if (this.fOpenAudio) {
            this.$Message.info("您已开启声音消息提醒!");
          }else {
            this.$Message.info("您已关闭声音消息提醒!");
          }
        },
        connect: function() {
            let self = this
            var socket = new SockJS(this.host+'/chat/chat-webSocket');
            stompClient = Stomp.over(socket);
            stompClient.debug = false;
            stompClient.connect({}, function(frame) {
              stompClient.subscribe('/user/' + self.msg.myId + '/' + self.orderId, function(response) {
                    self.otheritem = JSON.parse(response.body)
                    self.msgLists.push(self.otheritem)
                    if(self.fOpenNotice && window.Notification && Notification.permission == "granted") {
                        var notification = new Notification(self.msg.otherSide+"：", {
                            body: self.otheritem.content,
                            icon: 'https://bihuo-ex.oss-ap-southeast-1.aliyuncs.com/FAA55D97ED0370F08273C3A94F765C22.png'
                        });

                        notification.onclick = function() {
                            notification.close();
                        };
                    }
                    if(self.fOpenAudio) {
                        self.playAudio();
                    }
                });
            });
        },
        sendName: function() {
            if (this.mytext) {
                let self = this
                var content = this.mytext
                var jsonParam = {
                    'uidTo': this.msg.hisId,
                    'uidFrom': this.msg.myId,
                    'orderId': this.orderId,
                    'nameFrom': this.user.username,
                    'nameTo': this.msg.otherSide,
                    'content': content,
                };

                stompClient.send("/app/message/chat", {}, JSON.stringify(jsonParam));
                self.myitem = jsonParam
                self.myitem['timeNow'] = self.CurentTime()
                self.msgLists.push(self.myitem)
                this.scrollToBottom()
                self.mytext = ''
            } else {
                this.$Message.info(this.$t('otc.chatline.contentmsg'));
            }
        },
        CurentTime: function() {
            var now = new Date();

            var year = now.getFullYear();       //年
            var month = now.getMonth() + 1;     //月
            var day = now.getDate();            //日

            var hh = now.getHours();            //时
            var mm = now.getMinutes();          //分

            var clock = year + "-";

            if (month < 10)
                clock += "0";

            clock += month + "-";

            if (day < 10)
                clock += "0";

            clock += day + " ";

            if (hh < 10)
                clock += "0";

            clock += hh + ":";
            if (mm < 10) clock += '0';
            clock += mm;
            return clock;
        },
        getBefore: function(page) {
            let self = this
            let params = {}
            let selfarr = []
            let myarr = []
            params['orderId'] = this.orderId
            params['Page'] = this.currentPage
            this.$http.post(this.host+'/chat/getHistoryMessage', params).then(response => {
                var resp = response.body;
                this.totalPage = resp.totalPage
                if (resp.data) {
                    if (this.currentPage < resp.totalPage || this.currentPage == resp.totalPage) {
                        this.showMore = true
                        for (let i = 0; i < resp.data.length; i++) {
                            let objitem = {}
                            objitem = resp.data[i]
                            self.msgLists.unshift(objitem)
                        }
                        this.currentPage = this.currentPage - 0 + 1
                    } else {
                        this.showMore = false
                    }

                } else {
                    this.showMore = false
                }
            })
        },

    },
    created() {
        this.getBefore()
        this.scrollToBottom()
        this.connect()
    }
}
</script>
<style scoped>
  .msg-notice{
    width: 20%;
  }
  .msg-notice>label{
    margin-top: 8px;
  }
.chat-in-box .chat-in .chat-line .spe_show {
    /* background-color: #fdf1e9; */
    color: #ed2525;
    padding: 16px;
    margin-right: 3px;
    border:1px solid #27313e;
}

.chat-in-box .chat-in .chat-line {
    /* background-color: white; */
    padding: 100px 24px 100px 24px;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    border: 1px solid #27313e;
    width: 95%;
    margin: 0 auto;
    position: relative;
    min-height: 650px;
    transform: translate(0, 0);
}

#sysbox {
    position: fixed;
    top: 15px;
    left: 0;
    right: 0;
}

.chat-in-box .chat-in .chat-line .scroll-chat {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    height: 557px;
    overflow-y: auto;
    padding-right: 10px;
}

.chat-in-box .chat-in .chat-line .scroll-chat .more {
    text-align: center;
    color: #f0a70a;
}

.chat-in-box .chat-in .chat-line .scroll-chat .more span {
    cursor: pointer;
    padding: 6px 20px 6px 0;
}

.chat-container-box {
    margin-bottom: 20px;
    position: relative;
}

.chat-container-box h5 {
    text-align: center;
    font-size: 14px;
    padding: 10px 0;
}

.chat-container-box .user-chat-box .user-avatar-box,
.chat-container-box .user-chat-box {
    float: left;
}

.chat-container-box .user-chat-box .user-avatar-box,
.chat-container-box .user-chat-box {
    float: left;
}

.avatar-box {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
}

.user-avatar-public {
    background: #fff;
    height: 52px;
    width: 52px;
    box-shadow: 0 1px 5px 0 rgba(71, 78, 114, .24);
    position: relative;
}

.user-avatar-public>.user-avatar-in,
.user-avatar-public {
    border-radius: 50%;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
}

.user-avatar-public>.user-avatar-in {
    background: #f0a70a;
    height: 42px;
    width: 42px;
    color: #fff;
}

.chat-container-box .user-chat-box .user-content-box {
    max-width: 700px;
    float: left;
}

.chat-container-box .user-chat-box .user-content-box .user-name {
    padding-left: 16px;
    font-size: 12px;
    color: #8994a3;
    margin-bottom: 4px;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .user-desc {
    background-color: #f1f1f4;
    padding: 10px 16px;
    border-radius: 6px;
    margin-bottom: 0;
    margin-left: 16px;
    position: relative;
    font-size: 14px;
    word-break: break-all;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .times {
    padding-left: 16px;
    padding-right: 16px;
    margin-top: 6px;
    display: block;
    text-align: right;
    color: #c3c1c1;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .user-desc .icon {
    height: 0;
    width: 0;
    border: 8px solid transparent;
    border-right: 14px solid #f1f1f4;
    position: absolute;
    top: 6px;
    left: -20px;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .user-desc em {
    font-style: normal;
    font-size: 12px;
}

.chat-container-box .system-box {
    padding: 16px 24px;
    background-color: #27313e;
    border-radius: 7px;
    margin: 0 auto;
    margin-bottom: 20px;
    font-size: 16px;
    color: #ccc;
    max-width: 500px;
}

.chat-container-box .system-box .times {
    text-align: right;
    font-size: 12px;
}

.chat-container-box .user-chat-box.self-chat-box .user-avatar-box,
.chat-container-box .user-chat-box.self-chat-box {
    float: right;
}

.chat-container-box .user-chat-box.self-chat-box .user-content-box .user-name {
    text-align: right;
    padding-right: 16px;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .self-desc {
    margin-left: 0;
    margin-right: 16px;
    background-color: #f0a70a;
    color: #fff;
}

.chat-container-box .user-chat-box .user-content-box .chat-info .self-desc .icon {
    height: 0;
    width: 0;
    border: 8px solid transparent;
    border-left: 14px solid #f0a70a;
    position: absolute;
    top: 6px;
    right: -18px;
    left: unset;
}

.chat-in-box .chat-in .chat-line .send-msg-box {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    width: 94%;
    position: absolute;
    bottom: 21px;
    left: 25px;
    -webkit-box-shadow: 0 0 3px 1px rgba(0, 0, 0, 0.1);
    box-shadow: 0 0 3px 1px rgba(0, 0, 0, 0.1);
    border:1px solid #27313e;
}

.chat-in-box .chat-in .chat-line .send-msg-box .img-btn {
    position: relative;
    overflow: hidden;
    background-color: transparent;
    width: 10%;
    min-width: 60px;
    /* border: 1px solid #dfe8f2; */
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
    background: #ebeff5;
}

.chat-in-box .chat-in .chat-line .send-msg-box .img-btn .input-file {
    position: absolute;
    top: 0;
    left: 0;
    opacity: 0;
    width: 100%;
}

.chat-in-box .chat-in .chat-line .send-msg-box input {
    color: #fff;
    height: 60px;
    border: 1px solid transparent;
    border-left: none;
    background-color: transparent;
    padding: 10px;
    outline: none;
    width: 80%;
}

.chat-in-box .chat-in .chat-line .send-msg-box .send-btn {
    height: 60px;
    border: none;
    background-color: transparent;
    color: white;
    width: 12%;
    outline: none;
    font-size: 18px;
    cursor: pointer;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    background: #27313e;
}

.chat-in-box .chat-in .chat-line .send-msg-box .img-btn .img-icons {
    font-size: 24px;
    color: #f0a70a;
}

.chat-in-box .chat-in .chat-line .send-msg-box .send-btn .iconfont {
    font-size: 28px;
    color: #f0a70a;
}
</style>




