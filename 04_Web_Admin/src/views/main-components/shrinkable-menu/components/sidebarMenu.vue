<style lang="less">
@import "../styles/menu.less";
</style>

<template>
    <Menu ref="sideMenu" :active-name="$route.name" :open-names="openNames" :theme="menuTheme" width="auto" @on-open-change="clickMenu" @on-select="changeMenu" :accordion="true">
        <template v-for="item in menuList">

            <!-- <MenuItem v-if="item.children.length == 1" :name="item.children[0].name" :key="item.name">
                <Icon :type="item.icon" :size="iconSize"></Icon>
                <span class="layout-text">{{ itemTitle(item) }}</span>
            </MenuItem> -->

            <Submenu :name="item.name" :key="item.name">
                <template slot="title">
                    <Icon :type="item.icon" :size="iconSize"></Icon>
                    <span class="layout-text">{{ itemTitle(item) }}</span>
                </template>
                <template v-for="child in item.children">
                    <MenuItem :name="child.name" :key="child.name">
                    <Icon :type="child.icon" :size="iconSize"></Icon>
                    <span class="layout-text">{{ itemTitle(child) }}</span>
                    </MenuItem>
                </template>
            </Submenu>
        </template>
    </Menu>
</template>

<script>
import util from "@/libs/util.js";
export default {
  name: "sidebarMenu",
  props: {
    menuList: Array,
    iconSize: Number,
    menuTheme: {
      type: String,
      default: "dark"
    },
    openNames: {
      type: Array
    }
  },
  methods: {
    clickMenu(val) {
    //   if (val.length !== this.openedSubmenu.length) {
        this.$store.commit("resetOpenSubmenu", val);
    //   }
    },
    changeMenu(active) {
      this.$emit("on-change", active);
    },
    itemTitle(item) {
      if (typeof item.title === "object") {
        return this.$t(item.title.i18n);
      } else {
        return item.title;
      }
    }
  },
  computed: {
    openedSubmenu() {
      return this.$store.state.app.openedSubmenuArr;
    }
  },
  updated() {
    this.$nextTick(() => {
      if (this.$refs.sideMenu) {
        this.$refs.sideMenu.updateOpened();
      }
    });
  }
};
</script>
