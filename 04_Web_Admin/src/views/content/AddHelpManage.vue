<template>
<div>
  <Card>
    <p slot="title">
      系统帮助
    </p>
    <div class="formWrapper">
      <div class="titleWrapper">标题：
        <p class="title"><Input v-model="title"  style="width:500px"></Input></p>
      </div>

      <div class="createTimeWrapper" v-if="!!queryDetailId" >
        创建时间：
        <p class="title"><Input v-model="createTime" disabled></Input></p>
      </div>

      <div>分类：
        <Select v-model="klass" style="width:200px">
          <Option v-for=" perKlass in klassArr " :value="perKlass.value" :key="perKlass.value">{{ perKlass.name }}</Option>
        </Select>
      </div>
      <div class="titleWrapper">排序：
        <p class="title"><Input v-model="sort"  style="width:200px"></Input></p>
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
    </div>
    <smeditor :config='config' ref="smeditor" @isUploading = "ifUploading"></smeditor>

    <div class="btnWrapper">
      <Button type="success" long size='large' @click="upload">
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


import { BASICURL,  addHelpManage, helpManageDetail, updateHelpManage } from '@/service/getData';
import { getStore, removeStore, setStore } from '@/config/storage';


  export default {

    data () {
      return {
        klassArr: [
          { name: '新手指南', value: 0 },
          { name: '常见问题', value: 1 },
          { name: '交易指南', value: 2 },
          { name: '币种资料', value: 3 },
          { name: '行情技术', value: 4 },
          { name: '条款协议', value: 5 },
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
        sort: null,
        basicUrl: BASICURL,
        config :{
          uploadUrl: `${BASICURL}admin/common/upload/oss/image`,
          uploadName: 'file',
          parentName: 'helpManage',
          uploadParams: {

          },

          uploadCallback: (data) => {
            this.uploading = false;
            if(!data.code){
              this.$Message.success('上传成功!');
              return data.data;
            }else{
              this.$Message.error('上传失败!');
            }
          },
          uploadFailed: (err) => {
            console.log(err)
            this.uploading = false;
            this.$Message.error('上传失败!');

          }
        }
      }
    },
    methods: {
      ifUploading(val) {
        this.uploading = val;
      },
      changeAdStatus() {

      },
      upload() {
        this.$refs.smeditor.$emit('saveInner');

        let uploadObj = {
          title: this.title,
          sysHelpClassification: this.klass,
          status: this.status,
          lang: this.lang,
          sort: this.sort,
          content: getStore('smeditor')
        };

        let fn = null;
        if(this.ifAdd)  fn = addHelpManage;
        else {
					uploadObj.id = this.queryDetailId;
					uploadObj.createTime = this.createTime;
          fn = updateHelpManage;
        }

        if ( this.title ==='' || this.title ===null ||
            this.klass ==='' || this.klass ===null ||
            getStore('smeditor') ==='' || getStore('smeditor') === null ) {
              this.$Message.warning('请完善信息！')
        } else {
          fn(uploadObj)
          .then( res => {
            if(!res.code) {
              this.$Message.success('操作成功!');
              this.$router.push('/content/helpManage');
              removeStore('smeditor');
            }  else this.$Message.error('异常错误!');
          })
        }
      }
    },
    created() {
      removeStore('smeditor');
      this.queryDetailId = getStore('manageID');

      if (!!this.queryDetailId) {
        this.ifAdd = false;

        helpManageDetail({ id: this.queryDetailId })
       .then(res => {
         console.log(res);
         this.createTime = res.data.createTime;
         this.title = res.data.title;
         this.klass = res.data.sysHelpClassification;
         this.status = res.data.status;
         this.lang = res.data.lang;
         this.sort = res.data.sort;
          setStore('smeditor', res.data.content);

       })

      } else this.ifAdd = true;
      // this.textareaInner = getStore('smeditor');
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
    width: 200px;
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

