<template>
  <div>
    <Carousel autoplay v-model="value2" loop :autoplay-speed="3000" id="carousel">
        <CarouselItem>
            <div class="demo-carousel"><img src="../../assets/img/banner.jpg" alt=""></div>
        </CarouselItem>
        <CarouselItem>
            <div class="demo-carousel"><img src="../../assets/img/banner.jpg" alt=""></div>
        </CarouselItem>
        <CarouselItem>
            <div class="demo-carousel"><img src="../../assets/img/banner.jpg" alt=""></div>
        </CarouselItem>
    </Carousel>
  </div>
</template>
<script>
export default {
  data () {
      return {
            value2: 0
      }
  }
}
</script>
<style scoped>
#carousel img{
  width: 100%;
  height: 500px;
}
</style>


