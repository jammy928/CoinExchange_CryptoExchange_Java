<template>
  <div class="pdf">
    <embed src="https://bizzan.oss-cn-hangzhou.aliyuncs.com/BIZZANWhitePaperVer%201.0.pdf" width="100%" height="800">
    <p class="arrow" style="text-align:center;color:#000;margin: 20px 0 20px 0;">
      <a style="color: #FFF;" href="https://raw.githubusercontent.com/bizzanpublic/bizzan/master/BIZZANWhitePaperVer%201.0.pdf" target="_blank">{{$t("sectionPage.downloadwhite")}}</a>
    </p>
  </div>
</template>
<script>
export default {
  data() {
    return {
      src: 'https://bizzan.oss-cn-hangzhou.aliyuncs.com/BIZZANWhitePaperVer%201.0.pdf'
    };
  },
  created: function() {
    this.init();
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
      this.$store.state.HeaderActiveName = "1-8";
      this.$store.commit("navigate", "nav-whitepaper");
    },
    changePdfPage (val) {
      if (val === 0 && this.currentPage > 1) {
        this.currentPage--;
      }
      if (val === 1 && this.currentPage < this.pageCount) {
        this.currentPage++;
      }
    },
    loadPdfHandler (e) {
      this.currentPage = 1;
    }
  }
};
</script>

<style lang="scss" scoped>
.pdf{
  text-align:center;
  padding-top: 60px;
  width: 100%;
  padding-bottom: 15px;
  background: #51565b !important;
}
</style>
