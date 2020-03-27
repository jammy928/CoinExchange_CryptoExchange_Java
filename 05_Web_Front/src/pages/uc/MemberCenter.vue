<style lang="scss" scoped>
.mymsg_wrapper {
  padding: 70px 8% 0;
  margin-bottom: 10px;
  .wrapper {
    width: 100%;
    background: #192330;
    overflow: hidden;
    padding: 30px 15px 30px 0 ;
    ul.ivu-menu.ivu-menu-light.ivu-menu-vertical {
      padding: 0 10px;
      text-align:left;
      float: left;
      background: #192330;
      min-height: 662px;
      position: static;
      border-right: 1px solid #27313e;
      color: #fff;
      .title {
        text-align: center;
        font-size: 20px;
        line-height: 40px;
        color: #fff;
        margin-bottom: 20px;
      }
    }
    ul.ivu-menu.ivu-menu-light {
      &:after {
        background-color: #192330;
      }
    }
    .nav-rights {
      width: 100%;
      float: left;
      min-height: 600px;
    }
  }
}
</style>
<style lang="scss">
.mymsg_wrapper {
  .wrapper {
    ul.ivu-menu.ivu-menu-light.ivu-menu-vertical {
      li.ivu-menu-submenu {
        .ivu-menu-submenu-title {
          height: 50px;
          padding: 15px 10px 15px 10px;
          &:hover {
            background-color: #3c4553;
            .isclick {
              background-color: #fff;
            }
            .content,.ivu-icon {
              color: #fff;
            }
          }
          .isclick {
            width: 6px;
            height: 6px;
            background-color: #f0ac19;
            display: inline-block;
            border-radius: 50%;
            margin-top: 16px;
            line-height: 50px;
            display:none;
          }
          .content {
            padding-left: 5px;
            padding-top: 5px;
            color:#fff;
          }
        }
        .ivu-menu {
          li.ivu-menu-item.ivu-menu-item-active.ivu-menu-item-selected {
            color: #f0ac19;
            a {
              color: #f0ac19;
            }
          }
          li.ivu-menu-item {
            color: #fff;
            background: #27313e;
            padding: 0;
            text-align: left;
            a {
              color: #fff;
              display: block;
              height: 100%;
              padding: 14px 24px;
              font-size: 14px;
            }
          }
        }
      }
      li.ivu-menu-submenu.ivu-menu-opened {
        .ivu-menu-submenu-title {
          background-color: #3c4553;
          .isclick {
            background-color: #fff;
          }
          .content,
          .ivu-icon {
            color: #fff;
          }
        }
      }
    }
  }
}
.header_nav_mobile_triggle{
  display: none;
  margin-bottom: 20px;
}

@media screen and (max-width:768px){
  .pc_menu{
    display: none!important;
  }
  .header_nav_mobile_triggle{
    display: block!important;
    text-align:left;
    padding-left: 20px;
  }
  .mymsg_wrapper{
    padding: 40px 0!important;
  }
  .mymsg_wrapper .wrapper{
    padding: 0px 0px 30px 0px!important;
  }
  .mymsg_wrapper .wrapper .nav-rights{
    width: 100%!important;
  }

  .mymsg_wrapper .account-box .account-in .account-item .account-item-in{
    padding: 15px 5px 15px 5px!important;
  }
  .mymsg_wrapper .account-box .account-in .account-item .account-item-in .bankInfo{
    width: 50%!important;
  }
  .mymsg_wrapper .account-box .account-in .account-item .account-item-in .card-number{
    width: 100px!important;
  }
  .mymsg_wrapper .nav-right{
    padding-left: 0px!important;
  }
}

.uc_header_nav .ivu-menu-light.ivu-menu-vertical .ivu-menu-item-active:not(.ivu-menu-submenu){
    text-align: left;
    padding-left: 20px!important;
}
.uc_header_nav .ivu-menu-light.ivu-menu-vertical .ivu-menu-item-active:not(.ivu-menu-submenu) a{
  color: #f0a70a!important;
}
.uc_header_nav .ivu-menu-light.ivu-menu-vertical .ivu-menu-item:not(.ivu-menu-submenu){
    text-align: left;
    padding-left: 20px!important;
    color: #DDD;
}

.uc_header_nav .ivu-menu-light.ivu-menu-vertical .ivu-menu-item:not(.ivu-menu-submenu) a{
    color: #828ea1;
}
</style>

<template>
  <div class="mymsg_wrapper">
    <div class="wrapper">
      <Row :gutter="20" style="min-height: 600px;padding-top: 10px;">
        <Col :xs="24" :sm="24" :md="4" :lg="4">
          <div class="header_nav_mobile_triggle" @click="toggleMemu()">
            <Icon type="md-menu" style="font-size: 26px;color:#cccccc;"/> 个人中心
          </div>
          <Menu class="pc_menu" :active-name="activename" :open-names="opennames" @on-open-change="sss" ref="test" :accordion="true" style="width:100%;">
            <div class="title">{{$t('uc.member.usercenter')}}</div>
            <Submenu name="1">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.account')}}</span>
              </template>
              <MenuItem name="1-1">
              <router-link to="/uc/safe">{{$t('uc.member.securitysetting')}}</router-link>
              </MenuItem>
              <MenuItem name="1-2">
              <router-link to="/uc/account">{{$t('uc.member.accountsetting')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="2">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.assets')}}</span>
              </template>
              <MenuItem name="2-1">
              <router-link to="/uc/money">{{$t('uc.finance.personalassets')}}</router-link>
              </MenuItem>
              <MenuItem name="2-2">
              <router-link to="/uc/record">{{$t('uc.finance.billdetail')}}</router-link>
              </MenuItem>
              <MenuItem name="2-3">
              <router-link to="/uc/recharge">{{$t('uc.finance.charge')}}</router-link>
              </MenuItem>
              <MenuItem name="2-4">
              <router-link to="/uc/withdraw">{{$t('uc.finance.pickup')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="3">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.exchange')}}</span>
              </template>
              <MenuItem name="3-1">
              <router-link to="/uc/entrust/current">{{$t('uc.member.curdelegate')}}</router-link>
              </MenuItem>
              <MenuItem name="3-2">
              <router-link to="/uc/entrust/history">{{$t('uc.member.hisdelegate')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="4" style="display:none;">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.otc')}}</span>
              </template>
              <MenuItem name="4-1">
              <router-link to="/uc/ad">{{$t('otc.myad.title')}}</router-link>
              </MenuItem>
              <MenuItem name="4-2">
              <router-link to="/uc/order">{{$t('otc.myorder')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="5">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.promotion.title')}}</span>
              </template>
              <MenuItem name="5-1">
              <router-link to="/uc/promotion/mypromotion">{{$t('uc.promotion.subtitle3')}}</router-link>
              </MenuItem>
              <MenuItem name="5-2">
              <router-link to="/uc/promotion/mycards">{{$t('uc.promotion.subtitle1')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="6">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.activity.navTitle')}}</span>
              </template>
              <MenuItem name="6-1">
              <router-link to="/uc/innovation/myorders">{{$t('uc.activity.subNavTitle1')}}</router-link>
              </MenuItem>
              <MenuItem name="6-2">
              <router-link to="/uc/innovation/myminings">{{$t('uc.activity.subNavTitle2')}}</router-link>
              </MenuItem>
            </Submenu>
          </Menu>
        </Col>
        <Col :xs="24" :sm="24" :md="20" :lg="20">
          <router-view></router-view>
        </Col>
      </Row>
    </div>
    <Drawer :closable="true" width="40" v-model="ucNavDrawerModal" class="header_nav_mobile uc_header_nav">
      <Menu :active-name="activename" :open-names="opennames" @on-open-change="sss" ref="test" :accordion="true" placement="left" width="auto">
            <Submenu name="1">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.account')}}</span>
              </template>
              <MenuItem name="1-1">
              <router-link to="/uc/safe">{{$t('uc.member.securitysetting')}}</router-link>
              </MenuItem>
              <MenuItem name="1-2">
              <router-link to="/uc/account">{{$t('uc.member.accountsetting')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="2">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.assets')}}</span>
              </template>
              <MenuItem name="2-1">
              <router-link to="/uc/money">{{$t('uc.finance.personalassets')}}</router-link>
              </MenuItem>
              <MenuItem name="2-2">
              <router-link to="/uc/record">{{$t('uc.finance.billdetail')}}</router-link>
              </MenuItem>
              <MenuItem name="2-3">
              <router-link to="/uc/recharge">{{$t('uc.finance.charge')}}</router-link>
              </MenuItem>
              <MenuItem name="2-4">
              <router-link to="/uc/withdraw">{{$t('uc.finance.pickup')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="3">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.exchange')}}</span>
              </template>
              <MenuItem name="3-1">
              <router-link to="/uc/entrust/current">{{$t('uc.member.curdelegate')}}</router-link>
              </MenuItem>
              <MenuItem name="3-2">
              <router-link to="/uc/entrust/history">{{$t('uc.member.hisdelegate')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="4" style="display:none;">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.member.otc')}}</span>
              </template>
              <MenuItem name="4-1">
              <router-link to="/uc/ad">{{$t('otc.myad.title')}}</router-link>
              </MenuItem>
              <MenuItem name="4-2">
              <router-link to="/uc/order">{{$t('otc.myorder')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="5">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.promotion.title')}}</span>
              </template>
              <MenuItem name="5-1">
              <router-link to="/uc/promotion/mypromotion">{{$t('uc.promotion.subtitle3')}}</router-link>
              </MenuItem>
              <MenuItem name="5-2">
              <router-link to="/uc/promotion/mycards">{{$t('uc.promotion.subtitle1')}}</router-link>
              </MenuItem>
            </Submenu>
            <Submenu name="6">
              <template slot="title">
                <span class="isclick"></span>
                <span class="content">{{$t('uc.activity.navTitle')}}</span>
              </template>
              <MenuItem name="6-1">
              <router-link to="/uc/innovation/myorders">{{$t('uc.activity.subNavTitle1')}}</router-link>
              </MenuItem>
              <MenuItem name="6-2">
              <router-link to="/uc/innovation/myminings">{{$t('uc.activity.subNavTitle2')}}</router-link>
              </MenuItem>
            </Submenu>
          </Menu>
    </Drawer>
  </div>
</template>
<script>
export default {
  components: {},
  data() {
    return {
      activename: "1-1",
      opennames: ["1"],
      routeArr: {
        "/uc/safe": "1-1",
        "/uc/account": "1-2",
        "/uc/money": "2-1",
        "/uc/record": "2-2",
        "/uc/recharge": "2-3",
        "/uc/withdraw": "2-4",
        "/uc/trade": "2-5",
        "/uc/paydividends": "2-6",
        "/uc/blc": "2-7",
        "/uc/bjc": "2-8",
        "/uc/entrust/current": "3-1",
        "/uc/entrust/history": "3-2",
        "/uc/ad": "4-1",
        "/uc/order": "4-2",
        "/uc/withdraw/address":"2-4",
        "/uc/ad/create":"4-3",
        "/uc/ad/update":"4-4",
        "/uc/promotion/mypromotion":"5-1",
        "/uc/promotion/mycards":"5-2",
        "/uc/innovation/myorders":"6-1",
        "/uc/innovation/myminings":"6-2"
      },
      ucNavDrawerModal: false
    };
  },
  created: function() {
    this.init();
    const path = this.$route.path;
    this.heightLightMenu(path);
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-other");
      this.$store.state.HeaderActiveName = "0";
      this.$store.state.HeaderActiveName = "1-6";
      if (!localStorage.TOKEN || !localStorage.MEMBER) {
        this.$Message.success(this.$t("common.logintip"));
        this.$router.push("/login");
      }
    },
    toggleMemu(){
      this.ucNavDrawerModal = !this.ucNavDrawerModal;
    },
    sss(name) {
      let index = 1;
      if (name.length >= 1) {
        index = name[name.length - 1];
        this.opennames = [index];
        this.activename = index + "-1";
        this.link(this.activename);
      } else {
        return;
      }
    },
    link(code) {
      switch (code) {
        case "1-1":
          this.$router.push("/uc/safe");
          break;
        case "2-1":
          this.$router.push("/uc/money");
          break;
        case "3-1":
          this.$router.push("/uc/entrust/current");
          break;
        case "4-1":
          this.$router.push("/uc/ad");
          break;
        case "5-1":
          this.$router.push("/uc/promotion/mypromotion");
          break;
        case "5-2":
          this.$router.push("/uc/promotion/mycards");
          break;
        case "6-1":
          this.$router.push("/uc/innovation/myorders");
          break;
        case "6-2":
          this.$router.push("/uc/innovation/myminings");
          break;
        default:
          this.$router.push("/uc/safe");
          break;
      }
    },
    heightLightMenu(path) {
      let acName = this.routeArr[path] || "1-1",
        opName = acName.split("-")[0];
      this.opennames = [opName];
      this.activename = acName;
      this.$nextTick(function() {
        this.$refs.test.updateOpened();
        this.$refs.test.updateActiveName();
      });
    }
  },
  watch: {
    $route(to, form) {
      console.log(to.path);
      this.heightLightMenu(to.path);
    }
  },
  mounted: function() {
    this.$nextTick(function() {
      this.$refs.test.updateOpened();
      this.$refs.test.updateActiveName();
    });
  }
};
</script>
