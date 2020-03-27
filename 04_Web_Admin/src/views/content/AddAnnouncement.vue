<template>
<div>
  <Card>
    <p slot="title">
      编辑公告
    </p>
    <div class="formWrapper">
      <div class="titleWrapper">
        标题：
        <p class="title"><Input v-model="title"></Input></p>
      </div>
      <div>分类：
        <Select v-model="klass" style="width:200px">
          <Option v-for=" perKlass in klassArr " :value="perKlass.value" :key="perKlass.value">{{ perKlass.name }}</Option>
        </Select>
      </div>
      <div class="createTimeWrapper" v-if="!!queryDetailId" >
        创建时间：
        <p class="title"><Input v-model="createTime" disabled></Input></p>
      </div>

      <div>状态：
        <RadioGroup v-model="status" @on-change="changeAdStatus">
          <Radio :label="0">
            <span>显示</span>
          </Radio>
          <Radio :label="1">
            <span>不显示</span>
          </Radio>
        </RadioGroup>
      </div>
      <div>语言：
        <RadioGroup v-model="lang">
          <Radio label="CN">
            <span>中文</span>
          </Radio>
          <Radio label="EN">
            <span>English</span>
          </Radio>
        </RadioGroup>
      </div>
    </div>
    <smeditor :config='config' ref="smeditor" @isUploading = "ifUploading"></smeditor>

    <div class="btnWrapper">
      <Button type="success" :disabled="false" long size='large' @click="upload">
        提交
      </Button>
    </div>

    <div class="circleWrapper" v-show="uploading">
      <p>图片上传中...</p>
    </div>

  </Card>

</div>

</template>

<script>

import smeditor from '@/SMEditor/SMEditor.vue'

import { BASICURL,  addHelpManage, helpManageDetail, addAnnounce, updateAnnounce, announceDetail } from '@/service/getData';
import { getStore, removeStore, setStore } from '@/config/storage';


  export default {

    data () {
      return {
        klassArr: [
          { name: '一般公告', value: 0 },
          { name: '活动公告', value: 1 },
          { name: '充提公告', value: 2 },
          { name: '系统公告', value: 3 },
          { name: '上币公告', value: 4 },
          { name: '投票公告', value: 5 },
          { name: '其他', value: 6 },
        ],
        uploading: false,
        ifAdd: true,
        queryDetailId: null,
        createTime: null,
        title: null,
        klass: null,
        status: 0,
        lang: "CN",
        basicUrl: BASICURL,
        config :{
          uploadUrl: `${BASICURL}admin/common/upload/oss/image`,
          uploadName: 'file',
          parentName: 'announce',
          uploadParams: {

          },

          // 上传成功回调
          uploadCallback: (data) => {
            this.uploading = false;
            if(!data.code){
              this.$Message.success('上传成功!');
              return data.data;
            }else{
              this.$Message.error('上传失败!');
            }
          },
          // 上传失败回调, 可选
          uploadFailed: (err) => {
            this.uploading = false;
            console.log(err)
            this.$Message.error('上传失败!');
          }
        }
      }
    },
    methods: {
       ifUploading(val) {
        this.uploading = val;
      },
      changeAdStatus(val) {
        this.status = val*1;
      },
      upload() {
        this.$refs.smeditor.$emit('saveInner');

        let content = getStore('smeditor');

        let bol = true;

        if (this.status===0)  bol = true;
        else bol = false;

        if(this.title===''|| this.title===null || this.klass ==='' || this.klass ===null ||
            content===''|| content===null  ) {
            this.$Message.warning('请完善信息!');
        } else {
          if (this.ifAdd) {
            addAnnounce({ title: this.title, content: content, isShow: bol, lang: this.lang, announcementClassification: this.klass })
            .then( res => {
              if (!res.code) {
                this.$Message.success('操作成功!');
                this.$router.push('/content/announcemanage');
              } else this.$Message.error('异常错误!');
            })
          }else {
            updateAnnounce( this.queryDetailId ,{ title: this.title, content: content, isShow: bol, lang: this.lang, announcementClassification: this.klass})
             .then( res => {
              if (!res.code) {
                this.$Message.success('操作成功!');
                this.$router.push('/content/announcemanage');
              } else this.$Message.error('异常错误!');
            })
          }
        }
      }
    },
    created() {
      removeStore('smeditor');

      this.queryDetailId = getStore('manageID');

      if (!!this.queryDetailId) {

        this.ifAdd = false;

        announceDetail(this.queryDetailId)
       .then(res => {
         this.createTime = res.data.createTime;
         this.title = res.data.title;
         this.lang = res.data.lang;
         this.klass = res.data.announcementClassification;
         if(res.data.isShow) this.status = 0;
         else this.status = 1;

        setStore('smeditor', res.data.content);

       });
      }else  this.ifAdd = true;
    },

    components: {
      smeditor
    },


  }
</script>

<style lang="less" scoped>
  .wrapper{
    margin: auto;
    width: 70%;
  }
  .formWrapper{
    margin: auto;
    width: 70%;
  }
  .btnWrapper{
    margin: 20px auto;
    width: 30%;

  }
  .formWrapper{
    p, div{
      margin: 5px 0;
      Input{
        display: inline-block;
        width: 90px;
      }
    }
  }
  .title{
    display: inline-block;
    width: 500px;
  }
  .circleWrapper{
    position: absolute;
    z-index: 10;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, .3);
    p{
      font-size: 30px;
      font-family: '黑体';
      color: #fff;
      text-align: center;
      line-height: 550px;
    }
  }
</style>

