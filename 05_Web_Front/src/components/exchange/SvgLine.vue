<template>
    <svg :style="{ height: opts.height + 'px', width: opts.width + 'px' }">
        <polygon :fill="pColor" :points="polygonPoints">
        </polygon>
       <polyline fill="none" :points="polylinePoints" :stroke="sColor" :stroke-width="opts.strokeWidth" stroke-linecap="square">
       </polyline>
    </svg>
</template>
<script>
export default {
    data:function(){
        return {
          coords:[],
          opts:{strokeWidth:1},
          pColor:'#c6d9fd',
          sColor:'#4d89f9'
        }
    },
    props:{
        values:{
            type:Array,
            required:true
        },
        width:{
            type:Number,
            required:false
        },
        height:{
            type:Number,
            required:false
        },
        rose:{
          type:String,
          required:false
        }
    },
    created:function(){
        this.opts.width = this.width || 120;
        this.opts.height = this.height || 50;
        this.opts.rose = this.rose || 0;
        if (parseFloat(this.opts.rose) < 0) {
          this.pColor = "#f39494";
          this.sColor = "#e67f7f";
        }else {
          this.pColor = "#91baa7";
          this.sColor = "#66a488";
        }
        this.draw();
    },
    computed:{
        polylinePoints:function(){
          return this.coords.slice(2, this.coords.length - 2).join(' ');
        },
      polygonPoints:function(){
          return this.coords.join( );
      }
  },
  mounted:function(){

  },
  methods:{
      draw(){
        var opts = this.opts;
        var values = this.values;
        if (values.length == 1) values.push(values[0])
        var max = Math.max.apply(Math, opts.max == undefined ? values : values.concat(opts.max))
            , min = Math.min.apply(Math, opts.min == undefined ? values : values.concat(opts.min))

        var strokeWidth = opts.strokeWidth
            , width = opts.width
            , height = opts.height - strokeWidth
            , diff = max - min

        var xScale = this.x = function(input) {
            return input * (width / (values.length - 1))
        }

        var yScale = this.y = function(input) {
            var y = height

            if (diff) {
                y -= ((input - min) / diff) * height
            }

            return y + strokeWidth / 2
        }

        var zero = yScale(Math.max(min, 0));
        this.coords = [];
        this.coords = [0, zero]

        for (var i = 0; i < values.length; i++) {
            this.coords.push(
            xScale(i),
            yScale(values[i])
            )
        }

        this.coords.push(width, zero);
      }
  }
}
</script>
