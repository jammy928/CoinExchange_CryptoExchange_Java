<style lang="less">
@import "./403.less";
</style>

<template>
    <div class="error403">
        <div class="error403-body-con">
            <Card>
                <div class="error403-body-con-title">4<span class="error403-0-span"><Icon type="android-lock"></Icon></span><span class="error403-key-span"><Icon size="220" type="ios-bolt"></Icon></span></div>
                <p class="error403-body-con-message">You don't have permission</p>
                <div class="error403-btn-con">
                    <Button @click="goLogin" size="large" style="width: 200px;" type="text">重新登录</Button>
                    <Button @click="goHome" size="large" style="width: 200px;margin-left: 40px;" type="primary">返回首页</Button>
                </div>
            </Card>
        </div>
    </div>
</template>

<script>
import Cookies from 'js-cookie';
export default {
  name: "Error403",
  methods: {
    goHome() {
			this.$router.push({ name: "home_index" });
    },
    goLogin() {
			Cookies.remove('user');
			Cookies.remove('userInfo');
			Cookies.remove('userPhone');
      this.$router.push({
        name: "login"
      });
    }
  }
};
</script>
