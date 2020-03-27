import Cookies from 'js-cookie';

const user = {
  state: {
    pageLoading: false,
    memberCheckMask: false,
    memberCheckImgInfo: {},
    date: '',
    businessCheckMask: false,
    businessCheckInfo: {},
  },
  mutations: {
    logout(state, vm) {
      Cookies.remove('user');
      Cookies.remove('password');
      Cookies.remove('access');
      // 恢复默认样式
      let themeLink = document.querySelector('link[name="theme"]');
      themeLink.setAttribute('href', '');
      // 清空打开的页面等数据，但是保存主题数据
      let theme = '';
      if (localStorage.theme) {
        theme = localStorage.theme;
      }
      localStorage.clear();
      if (theme) {
        localStorage.theme = theme;
      }
    },
    switchBusinessMask(state, bol) {
      state.businessCheckMask = bol;
    },
    switchLoading(state, bol) {
      state.pageLoading = bol;
    },
    switchMemberMask(state, bol) {
      state.memberCheckMask = bol;
    },
    businessCheckInfo(state, obj) {
      state.businessCheckInfo = obj;
    },
    memeberCheckImg(state, obj) {
      state.memberCheckImgInfo = obj;
    },
    changeDate(state, str) {
      state.date = str;
    }
  }
};

export default user;