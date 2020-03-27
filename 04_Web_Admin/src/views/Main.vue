<style lang="less">
@import "./main.less";
</style>
<template>
    <div class="main" :class="{'main-hide-text': shrink}">
        <div class="sidebar-menu-con" :style="{width: shrink?'60px':'170px', overflow: shrink ? 'visible' : 'auto'}">
            <shrinkable-menu
                :shrink="shrink"
                @on-change="handleSubmenuChange"
                :theme="menuTheme"
                :before-push="beforePush"
                :open-names="openedSubmenuArr"
                :menu-list="menuList">
                <div slot="top" class="logo-con">
                    <div v-show="!shrink">
                      <img v-if="darkImgOrLightImg" src="../images/logo.png" key="max-logo" />
                      <img v-if="!darkImgOrLightImg"src="../images/logo_light.png" key="max-logo"/>
                    </div>
                    <div v-show="shrink">
                      <img v-if="darkImgOrLightImg" src="../images/logo-min.png" key="max-logo" />
                      <img v-if="!darkImgOrLightImg"src="../images/logo-min-light.png" key="max-logo" />
                    </div>
                </div>
            </shrinkable-menu>
        </div>
        <div class="main-header-con" :style="{paddingLeft: shrink?'60px':'170px'}">
            <div class="main-header">
                <div class="navicon-con">
                    <Button :style="{transform: 'rotateZ(' + (this.shrink ? '-90' : '0') + 'deg)'}" type="text" @click="toggleClick">
                        <Icon type="navicon" size="32"></Icon>
                    </Button>
                </div>
                <div class="header-middle-con">
                    <div class="main-breadcrumb">
                        <breadcrumb-nav :currentPath="currentPath"></breadcrumb-nav>
                    </div>
                </div>
                <div class="header-avator-con">
                    <full-screen v-model="isFullScreen" @on-change="fullscreenChange"></full-screen>
                    <lock-screen></lock-screen>
                    <!-- 暂缺 -->
                    <!-- <message-tip v-model="mesCount"></message-tip> -->
                    <theme-switch style="display:none;"></theme-switch>

                    <div class="user-dropdown-menu-con">
                        <Row type="flex" justify="end" align="middle" class="user-dropdown-innercon">
                            <Dropdown transfer trigger="click" @on-click="handleClickUserDropdown">
                                <a href="javascript:void(0)">
                                    <span class="main-user-name">{{ userName }}</span>
                                    <Icon type="arrow-down-b"></Icon>
                                </a>
                                <DropdownMenu slot="list">
                                  <DropdownItem name="ownSpace">个人中心</DropdownItem>
                                  <DropdownItem name="loginout" divided class="resetexit">退出登录</DropdownItem>
                                </DropdownMenu>
                            </Dropdown>
                            <Avatar :src="avatorPath" style="background-color:#e96500;margin-left: 10px;"></Avatar>
                        </Row>
                    </div>
                </div>
            </div>
            <div class="tags-con">
                <tags-page-opened :pageTagsList="pageTagsList" @getopen="test"></tags-page-opened>
            </div>
        </div>
        <div class="single-page-con" :style="{left: shrink?'60px':'170px'}">
            <div class="single-page">
                <keep-alive :include="cachePage">
                    <router-view></router-view>
                </keep-alive>
            </div>
            <!-- <div class="search-mask">
              搜索中
            </div> -->
        </div>
    </div>
</template>
<script>
import shrinkableMenu from "./main-components/shrinkable-menu/shrinkable-menu.vue";
import tagsPageOpened from "./main-components/tags-page-opened.vue";
import breadcrumbNav from "./main-components/breadcrumb-nav.vue";
import fullScreen from "./main-components/fullscreen.vue";
import lockScreen from "./main-components/lockscreen/lockscreen.vue";
import messageTip from "./main-components/message-tip.vue";
import themeSwitch from "./main-components/theme-switch/theme-switch.vue";
import Cookies from "js-cookie";
import util from "@/libs/util.js";
import { setStore, getStore, removeStore } from "@/config/storage.js";
import { clearAppRouter } from '@/router/router'


export default {
  components: {
    shrinkableMenu,
    tagsPageOpened,
    breadcrumbNav,
    fullScreen,
    lockScreen,
    messageTip,
    themeSwitch
  },
  data() {
    return {
      shrink: false,
      userName: "",
      isFullScreen: false,
    };
  },
  computed: {
    darkImgOrLightImg(){
      if(this.$store.state.app.menuTheme === "dark"){
        return true
      }else{
        return false
      }
    },
		openedSubmenuArr() {
			return this.$store.state.app.openedSubmenuArr;
		},
    menuList() {
      return this.$store.state.app.menuList;
    },
    pageTagsList() {
      return this.$store.state.app.pageOpenedList; // 打开的页面的页面对象
    },
    currentPath() {
      return this.$store.state.app.currentPath; // 当前面包屑数组
    },
    avatorPath() {
      return localStorage.avatorImgPath;
    },
    cachePage() {
      return this.$store.state.app.cachePage;
    },
    lang() {
      return this.$store.state.app.lang;
    },
    menuTheme() {
      return this.$store.state.app.menuTheme;
    },
    mesCount() {
      return this.$store.state.app.messageCount;
    }
  },
  methods: {
    test(item){
      let a = JSON.parse(getStore("leftSidebarList"))
      let openobj = a.map((e, index)=>{
        if(e.id == item.parentId){
          return e.name
        }
      })
      openobj.map((e, index)=>{
        if(e){
          this.$store.commit("resetOpenSubmenu",[e])
        }else{
          this.$store.commit("resetOpenSubmenu",[])
        }
      })
      // this.openedSubmenuArr = ['system-------']

    },
    init() {
      let pathArr = util.setCurrentPath(this, this.$route.name);
      this.$store.commit("updateMenulist");
      if (pathArr.length >= 2) {
        this.$store.commit("addOpenSubmenu", pathArr[1].name);
      }
      this.userName = Cookies.get("user");
      let messageCount = 3;
      this.messageCount = messageCount.toString();
      this.checkTag(this.$route.name);
      this.$store.commit("setMessageCount", 3);
    },
    toggleClick() {
      this.shrink = !this.shrink;
    },
    handleClickUserDropdown(name) {
      if (name === "ownSpace") {
        //个人中心
       this.$router.push('/user/personalcenter')
      } else if (name === "loginout") {
        // 退出登录
				removeStore("leftSidebarList");
				Cookies.remove('userInfo');
				Cookies.remove('userPhone');
				removeStore("pageOpenedList");
        this.$store.commit("logout", this);
        this.$store.commit("clearOpenedSubmenu");
        this.$store.commit('clearAllTags');
        clearAppRouter();
        this.$router.push({
          name: "login"
        });
      }
    },
    checkTag(name) {
      let openpageHasTag = this.pageTagsList.some(item => {
        if (item.name === name) {
          return true;
        }
      });
      if (!openpageHasTag) {
        //  解决关闭当前标签后再点击回退按钮会退到当前页时没有标签的问题
        util.openNewPage(
          this,
          name,
          this.$route.params || {},
          this.$route.query || {}
        );
      }
    },
    handleSubmenuChange(val) {
      // console.log(val)
    },
    beforePush(name) {
      return true;
    },
    fullscreenChange(isFullScreen) {
      // console.log(isFullScreen);
    }
  },
  watch: {
    $route(to) {
      this.$store.commit("setCurrentPageName", to.name);
			let pathArr = util.setCurrentPath(this, to.name);
      if (pathArr.length > 2) {
				this.$store.commit("addOpenSubmenu", pathArr[1].name);
      }
      this.checkTag(to.name);
      localStorage.currentPageName = to.name;
    },
    lang() {
      // util.setCurrentPath(this, this.$route.name); // 在切换语言时用于刷新面包屑
    }
  },
  mounted() {
    this.init();
  },
  created() {
    this.$store.commit("updateMenulist");
    // 显示打开的页面的列表
    this.$store.commit("setOpenedList");

  }
};
</script>
