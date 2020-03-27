<template>
	<div class="home">
		<Row type="flex" justify="space-between" class="dashBoard">
			<Col span="7">
			<Card>
				<Row slot="title">
					<Col span="8">用户类型</Col>
					<Col span="8">累计</Col>
					<Col span="8">昨日新增</Col>
				</Row>
				<div class="cardBody">
					<Row>
						<Col span="8">注册用户</Col>
						<Col span="8">{{ memberBoardInfo.registrationNum }}</Col>
						<Col span="8">{{ memberBoardInfo.yesterdayRegistrationNum }}</Col>
					</Row>
					<Row>
						<Col span="8">实名用户</Col>
						<Col span="8">{{ memberBoardInfo.applicationNum }}</Col>
						<Col span="8">{{ memberBoardInfo.yesterdayApplicationNum }}</Col>
					</Row>
					<Row>
						<Col span="8">认证商家</Col>
						<Col span="8">{{ memberBoardInfo.bussinessNum }}</Col>
						<Col span="8">{{ memberBoardInfo.yesterdayBussinessNum }}</Col>
					</Row>
					<!-- <Row>
							<Col span="8">交易用户</Col>
							<Col span="8">累计</Col>
							<Col span="8">今日新增</Col>
						</Row> -->
				</div>
			</Card>
			</Col>
			<Col span="8">
			<Card>
				<span slot="title">OTC交易</span>
				<div class="cardBody">
					<p>法币交易总交易量</p>
					<p class="tradeTotalNum">{{ otcChart.borderInfo.amount }}</p>
					<p>总交易额
						<span>{{ otcChart.borderInfo.money }}</span>
					</p>
					<p>总手续费
						<span class="fee">{{ otcChart.borderInfo.fee }}</span>
					</p>
					<div class="selectWrapper">
						<Select v-model="otcChart.board.unit" size="small" @on-change="switchOtcBoard">
							<Option :value="coin" v-for="(coin, index) in otcChart.allCoin" :key="index">{{ coin }}</Option>
						</Select>
					</div>
				</div>
				<div class="cardFooterWrapper">
					<Row class="cardFooter">
						<Col span="11" offset="1">
						<p>昨日交易量
							<span style="display:block;">{{ otcChart.borderInfo.yesterdayAmount }}</span>
						</p>
						</Col>
						<Col span="11" offset="1">
						<p>昨日手续费
							<span style="display:block;">{{ otcChart.borderInfo.yesterdayFee }}</span>
						</p>
						</Col>
					</Row>
				</div>
			</Card>
			</Col>
			<Col span="8">
			<Card>
				<span slot="title">币币交易</span>
				<div class="cardBody">
					<p>币币交易总交易量</p>
					<p class="tradeTotalNum">{{ exchangeChart.boardInfo.amount }}</p>
					<p style="line-height: 60px">总手续费
						<span class="fee">{{ exchangeChart.boardInfo.fee }}</span>
					</p>
					<div class="selectWrapper">
						<Select v-model="exchangeChart.board.unit" size="small" @on-change='test'>
							<Option :value="coin" v-for="(coin, index) in exchangeChart.allCoin" :key="index">{{ coin }}</Option>
						</Select>
					</div>
				</div>
				<div class="cardFooterWrapper">
					<Row class="cardFooter" justify="space-around">
						<Col span="11">
						<p>昨日交易量
							<span style="display:block;">{{ exchangeChart.boardInfo.yesterdayAmount }}</span>
						</p>
						</Col>
						<Col span="11">
						<p>昨日手续费
							<span style="display:block;">{{ exchangeChart.boardInfo.yesterdayFee }}</span>
						</p>
						</Col>
					</Row>
				</div>
			</Card>
			</Col>
		</Row>

		<Row class="register mainChart">
			<Card>
				<h3>注册、认证对比图
					<span> （查询日期不超过30天）</span>
				</h3>
				<Row class="chartFunction">
					<Col span="3" offset="15">
					<span :class="{ active: index === registerChart.timeRange.active }" v-for="(time, index) in registerChart.timeRange.time" :key="time" @click="selectTime(index, registerChart)">{{ time }}</span>
					</Col>
					<Col span="5">
					<DatePicker v-model="registerChart.initTime" size="small" type="daterange" @on-change="registerTimeRange">
					</DatePicker>
					</Col>
				</Row>

				<Row v-show="!!registerChart.chartData.length">
					<v-chart :forceFit="true" :height="height" :data="registerChart.chartData">
						<v-tooltip :crosshairs="{
							type: 'cross',
							style: {
								fill: 'red'
							}
						}" />
						<v-axis :dataKey="'时间'" :title="axisTitle"></v-axis>
						<v-axis :dataKey="'人数'" :title="axisTitle">
						</v-axis>
						<v-line position="时间*人数" :color="'类型'" />
						<v-point position="时间*人数" shape="circle" :color="'类型'" />
						<v-legend :color="'类型'" />
					</v-chart>
				</Row>

				<Row class="dataEmpty" v-show="!registerChart.chartData.length">
					<p>此时间区间内无数据！</p>
				</Row>
			</Card>
		</Row>

		<Row class="otcVolume mainChart">
			<Card>
				<h3>法币成交量趋势图
					<span> （查询日期不超过30天）</span>
				</h3>
				<Row class="chartFunction">
					<Col span="3" offset="15">
					<span :class="{ active: index === otcChart.timeRange.active }" v-for="(time, index) in otcChart.timeRange.time" :key="time" @click="selectTime(index, otcChart)">{{ time }}</span>
					</Col>
					<Col span="5">
					<DatePicker v-model="otcChart.initTime" size="small" type="daterange" @on-change="otcTimeRange">
					</DatePicker>
					</Col>
				</Row>

				<Row v-show="!!otcChart.chartData.length">
					<v-chart :forceFit="true" :height="height" :data="otcChart.chartData">
						<v-tooltip :crosshairs="{
							type: 'cross',
							style: {
								fill: 'red'
							}
						}" />
						<v-axis :dataKey="'时间'" :title="axisTitle"></v-axis>
						<v-axis :dataKey="'交易量'" :title="axisTitle">
						</v-axis>
						<v-line position="时间*交易量" :color="'币种'" />
						<v-point position="时间*交易量" shape="circle" :color="'币种'" />
						<v-legend :color="'币种'" />
					</v-chart>
				</Row>

				<Row class="dataEmpty" v-show="!otcChart.chartData.length">
					<p>此时间区间内无数据！</p>
				</Row>
			</Card>
		</Row>

		<Row class="exchangeVolume mainChart">
			<Card>
				<h3>币币成交量统计图</h3>
				<Row class="tradeArea">
					<p>
						<span class="title">交易区</span>
						<RadioGroup v-model="exchangeChart.tradeArea.active" @on-change="switchTradeCoin" size="large">
							<Radio v-for="(item, index) in exchangeChart.tradeArea.coin" :label="index" :key="item">
								<span>{{ item }}</span>
							</Radio>
						</RadioGroup>
					</p>
				</Row>
				<Row v-show="!!exchangeChart.chartData.length">
					<div ref="exchangeNumChart"></div>
				</Row>
				<Row class="dataEmpty" v-show="!exchangeChart.chartData.length">
					<p>暂无数据！</p>
				</Row>
			</Card>
		</Row>

		<Row class="exchangeVolume mainChart">
			<Card>
				<h3>币币交易额统计图</h3>
				<Row class="tradeArea">
					<p>
						<span class="title">交易区</span>
						<RadioGroup v-model="exchangeChart.tradeArea.active" @on-change="switchTradeCoin" size="large">
							<Radio v-for="(item, index) in exchangeChart.tradeArea.coin" :label="index" :key="item">
								<span>{{ item }}</span>
							</Radio>
						</RadioGroup>
					</p>
				</Row>
				<Row v-show="!!exchangeChart.chartData.length">
					<div ref="exchangeMoneyChart"></div>
				</Row>
				<Row class="dataEmpty" v-show="!exchangeChart.chartData.length">
					<p>暂无数据！</p>
				</Row>
			</Card>
		</Row>

		<!-- <Row>
			<div id="c1" ref="c1"></div>
		</Row> -->
	</div>
</template>

<script>
import {
  memberBoard,
  memberChart,
  otcChart,
  coinChart,
  otcBoard,
  coinBoard,
  allOtcCoin,
  allBaseCoin,
  allExchangeUnits
} from "@/service/getData";
import { DataSet } from "@antv/data-set";
import G2 from "@antv/g2";
import dtime from "time-formater";

export default {
  data() {
    return {
      memberBoardInfo: {},
      registerChart: {
        type: "register",
        initTime: [],
        chartData: [],
        timeRange: { active: 0, time: ["三天", "一周", "一月"] },
        searchDate: {
          startDate: dtime(Date.now() - 24 * 60 * 60000 * 3).format(
            "YYYY-MM-DD"
          ),
          endDate: dtime(Date.now() - 24 * 60 * 60000).format("YYYY-MM-DD")
        }
      },

      otcChart: {
        type: "otc",
        initTime: [],
        chartData: [],
        timeRange: { active: 0, time: ["三天", "一周", "一月"] },
        allCoin: [],
        board: {
          startDate: "",
          endDate: "",
          unit: ""
        },
        borderInfo: {
          amount: "",
          money: "",
          fee: ""
        },
        searchDate: {
          startDate: dtime(Date.now() - 24 * 60 * 60000 * 3).format(
            "YYYY-MM-DD"
          ),
          endDate: dtime(Date.now() - 24 * 60 * 60000).format("YYYY-MM-DD"),
          units: []
        }
      },

      exchangeChart: {
        type: "exchange",
        adjust: [
          {
            type: "dodge"
          }
        ],
        board: {
          startDate: "",
          endDate: "",
          unit: ""
        },
        boardInfo: {
          amount: "",
          fee: "",
          yesterdayAmount:'',
          yesterdayFee:''
        },
        allCoin: [],
        tradeArea: { active: 0, coin: [] },
        coinType: { active: [], coin: [] },
        chartData: [],
        searchDate: {
          // startDate: '2018-3-20',
          // endDate: '2018-4-19',
          startDate: dtime(Date.now() - 24 * 60 * 60000 * 7).format(
            "YYYY-MM-DD"
          ),
          endDate: dtime(Date.now() - 24 * 60 * 60000).format("YYYY-MM-DD"),
          baseSymbol: "", //交易结算币
          coinSymbols: [] //平台币种集合
          // coinSymbols: ['BTC', 'ETH']//平台币种集合
        }
      },
      coinType: 1,
      height: 400,
      axisTitle: {
        autoRotate: false,
        textStyle: {
          fill: "#444"
        },
        position: "end"
      }
    };
  },
  computed: {},
  methods: {
    test(){
      coinBoard(this.exchangeChart.board).then(res=>{
        if(!res.code){
          this.exchangeChart.boardInfo = res.data;
        }
      })
    },
    switchOtcBoard() {
      otcBoard(this.otcChart.board)
        .then(res => {
          if (!res.code) {
            this.otcChart.borderInfo = res.data;
          } else this.$Message.error(res.message);
        })
        .catch(err => console.log(err));
    },
    switchTradeCoin(val) {
      let unit = this.exchangeChart.tradeArea.coin[val];
      this.exchangeChart.searchDate.baseSymbol = unit;
      this.exchangeChartFn(this.exchangeChart.searchDate);
    },
    selectTime(index, obj) {
      obj.initTime = [];
      obj.timeRange.active = index;
      obj.searchDate.endDate = this.formatTime(1);
      if (index === 0) {
        obj.searchDate.startDate = this.formatTime(3);
      } else if (index === 1) {
        obj.searchDate.startDate = this.formatTime(7);
      } else if (index === 2) {
        obj.searchDate.startDate = this.formatTime(30);
      }

      if (obj.type === "register") {
        this.registerChartFn(obj.searchDate);
      } else if (obj.type === "otc") {
        obj.searchDate.units = this.otcChart.allCoin;
        this.otcChartFn(obj.searchDate);
      }
    },
    limitTime(time, obj) {
      let limitTime =
        dtime(time[1])["0"].getTime() - dtime(time[0])["0"].getTime();
      obj.timeRange.active = -1;
      if (limitTime > 30 * 24 * 60 * 60 * 1000) {
        obj.initTime = [];
        obj.initTime.push(obj.searchDate.startDate);
        obj.initTime.push(obj.searchDate.endDate);
        this.$Message.warning("只能查询30天内趋势变化！");
        return true;
      }
    },
    otcTimeRange(time) {
      if (this.limitTime(time, this.otcChart)) return;
      this.otcChart.searchDate.startDate = time[0];
      this.otcChart.searchDate.endDate = time[1];
      this.otcChart.searchDate.units = ["ETH", "USDT", "BTC"];
      this.otcChartFn(this.otcChart.searchDate);
    },
    registerTimeRange(time) {
      if (this.limitTime(time, this.registerChart)) return;
      this.registerChart.searchDate.startDate = time[0];
      this.registerChart.searchDate.endDate = time[1];
      this.registerChartFn(this.registerChart.searchDate);
    },
    exchangeManualChart(sourceData, obj) {
      const chart = new G2.Chart({
        container: obj.ref,
        width:1000
      });
      const dv = new DataSet().createView().source(sourceData);
      dv.transform({
        type: "map",
        callback(row) {
          row.date = dtime(row.date).format("MMMDo");
          return row;
        }
      });
      dv.transform({
        type: "rename",
        map: {
          coinSymbol: "币种",
          amount: "交易量",
          date: "时间",
          money: "交易额"
        }
      });
      chart.source(dv.rows);
      chart
        .interval()
        .position(`时间*${obj.type}`)
        .color("币种")
        .adjust("dodge");

      let legendArr = [];
      chart.filter("币种", val => {
        if (legendArr.indexOf(val) < 0) legendArr.push(val);
        if (legendArr.length > 1)
          return val === legendArr[0] || val === legendArr[1];
        else return val;
      });
      chart.legend("币种", {
        // position: 'right'
      });

      chart.render();
      this.exchangeChart.chartData = dv.rows;
    },
    exchangeChartFn(searchObj) {
      coinChart(searchObj).then(res => {
          if (!res.code) {
            this.exchangeManualChart(res.data, {
              ref: this.$refs.exchangeNumChart,
              type: "交易量"
            });
            this.exchangeManualChart(res.data, {
              ref: this.$refs.exchangeMoneyChart,
              type: "交易额"
            });
          } else this.$Message.error(res.message);
        })
        .catch(err => console.log(err));
    },
    otcChartFn(searchObj) {
      otcChart(searchObj)
        .then(res => {
          if (!res.code) {
            const dv = new DataSet.View().source(res.data);
            dv.transform({
              type: "rename",
              map: {
                unit: "币种",
                amount: "交易量",
                date: "时间"
              }
            });
            this.otcChart.chartData = dv.rows;
          } else this.$Message.error(res.message);
        })
        .catch(err => console.log(err));
    },
    registerChartFn(searchObj) {
      memberChart(searchObj)
        .then(res => {
          if (!res.code) {
            const dv = new DataSet.View().source(res.data);
            dv.transform({
              type: "rename",
              map: {
                registrationNum: "注册会员",
                applicationNum: "实名会员",
                bussinessNum: "认证会员",
                date: "时间"
              }
            });
            dv.transform({
              type: "fold",
              fields: ["注册会员", "实名会员", "认证会员"],
              key: "类型",
              value: "人数",
              retains: ["时间"]
            });
            this.registerChart.chartData = dv.rows;
          } else this.$Message.error(res.message);
        })
        .catch(err => console.log(err));
    },
    formatTime(day) {
      return dtime(Date.now() - 24 * 60 * 60000 * day).format("YYYY-MM-DD");
    }
  },
  created() {
    allOtcCoin().then(res => {
      if (!res.code) {
        this.otcChart.allCoin = res.data;
        this.otcChart.board.unit = res.data[0];
        this.otcChart.searchDate.units = res.data;
        this.otcChartFn(this.otcChart.searchDate);
        otcBoard(this.otcChart.board)
          .then(res => {
            if (!res.code) {
              this.otcChart.borderInfo = res.data;
            } else this.$Message.error(res.message);
          })
          .catch(err => console.log(err));
      } else this.$Message.error(res.message);
    });
    allBaseCoin().then(res => {
      if (!res.code) {
        this.exchangeChart.tradeArea.coin = res.data;
        this.exchangeChart.allCoin = res.data;
        this.exchangeChart.searchDate.baseSymbol = res.data[0];
        this.exchangeChartFn(this.exchangeChart.searchDate);
        this.exchangeChart.board.unit = res.data[0];
        coinBoard(this.exchangeChart.board).then(res => {
          if (!res.code) {
            this.exchangeChart.boardInfo = res.data;
          } else this.$Message.error(res.message);
        });
      } else this.$Message.error(res.message);
    });
    memberBoard().then(res => {
      if (!res.code) {
        this.memberBoardInfo = res.data[0];
      } else this.$Message.error(res.message);
    });
    this.registerChartFn(this.registerChart.searchDate);
  }
};
</script>

<style lang="less" scoped>
@subTitle: #80848f;
@activeColor: #2d8cf0;
@borderLine: 1px solid rgba(204, 204, 204, 0.4);
.home {
  padding: 0 20px;
  .dashBoard {
    .ivu-card {
      // height: 238px;
      height: 100%;
    }
  }
  .mainChart {
    h3 {
      span {
        font-size: 12px;
        font-weight: normal;
        line-height: 12px;
        color: @subTitle;
      }
    }
    .tradeArea {
      margin: auto;
      padding: 10px 0 10px 15px;
      width: 35%;
      border: @borderLine;
      p {
        position: relative;
        padding-left: 60px;
        line-height: 30px;
        span.title {
          position: absolute;
          left: 0;
          margin-right: 15px;
          font-size: 16px;
          line-height: 32px;
        }
      }
    }
    .dataEmpty {
      text-align: center;
      line-height: 50px;
      color: @subTitle;
    }
    .chartFunction {
      line-height: 50px;
      span {
        margin-right: 10px;
        font-size: 15px;
        cursor: pointer;
        &.active {
          color: @activeColor;
          font-weight: 600;
        }
      }
    }
  }
  & > div {
    margin-top: 30px;
  }
  .ivu-col {
    text-align: center;
    .cardFooterWrapper {
      margin: auto;
      width: 98%;
      border-top: 1px solid#ece8e8;
      .cardFooter {
        color: @subTitle;
        p {
          margin-top: 10px;
          line-height: 30px;
        }
      }
    }
    .cardBody {
      position: relative;
      text-align: left;
      color: @subTitle;
      & > p {
        line-height: 30px;
      }
      .fee {
        color: #ec0909;
      }
      .tradeTotalNum {
        color: #444;
        font-size: 30px;
        word-break: break-word;
      }
      & > .ivu-row {
        line-height: 40px;
      }
      .selectWrapper {
        position: absolute;
        top: 3px;
        right: 10px;
        width: 80px;
      }
    }
  }
}
</style>
