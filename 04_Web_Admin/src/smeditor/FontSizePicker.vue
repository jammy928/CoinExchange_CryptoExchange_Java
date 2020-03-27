<template>
  <div class="font-size-picker-options">
    <ul>
      <li v-for='(size, index) in sizes' v-on:click='handleFontSizeClick(size, index)' v-bind:class="{fontSizeActive: fontSize === size}">{{size}}</li>
    </ul>
  </div>
</template>
<script type="text/javascript">

export default {
  name: 'FontSizePicker',
  data () {
    return {
      sizes: [10, 13, 16, 18, 24, 32, 48],
      fontSize: 16
    }
  },
  props: ['FontSizePickerClick'],
  methods: {
    handleFontSizeClick (size, index) {
      this.fontSize = size
      this.FontSizePickerClick(size, index)
    }
  }
}
</script>

<style type="text/css">
.font-size-picker-options {
  width: 60px;
  text-align: justify;
  padding: 5px 0px;
  border-radius: 2px;
  position: absolute;
  box-shadow: 0 1px 2px #ccc;
  background-color: #fff;
  margin-top: 10px;
  text-align: center;
}

.font-size-picker-options ul {
  list-style: none;
  padding: 0px;
  margin: 0px;
  font-size: 14px;
  color: #333;
}

.font-size-picker-options ul li {
  padding: 5px;
  transition: all 0.3s;
}

.font-size-picker-options ul li:hover, .fontSizeActive {
  background-color: #E5E5E5;
}


</style>
