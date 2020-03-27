
<template>
    <div>
        <Row class="expand-row">
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.tradingtime')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.transactionpair')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.direction')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.price')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.entrustment')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.havedeal')}}</span>
            </Col>
            <Col span="3">
                <span class="expand-key">{{$t('uc.finance.trade.serviceCharge')}}</span>
            </Col>
        </Row>
        <div v-for="item in row">
            <Row class="expand-row">
                <Col span="3">
                    <span class="expand-key">{{item.time}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.BHB}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.xian}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.usdt}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.HB}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.H}}</span>
                </Col>
                <Col span="3">
                    <span class="expand-key">{{item.B}}</span>
                </Col>
        </Row>
        </div>
        <!--<Row class="expand-row">-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">2018-0629 14:00</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">BHB/USDT</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">限价卖出</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">0.01USDT</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">1000BHB</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">1000BHB</span>-->
            <!--</Col>-->
            <!--<Col span="3">-->
                <!--<span class="expand-key">1BHB</span>-->
            <!--</Col>-->
        <!--</Row>-->
    </div>
</template>
<script>
    export default {
        props: {
            row: Object
        },
    };
</script>
<style scoped>
    .expand-row{
        margin-bottom: 16px;
    }
    .expand-row .ivu-col-span-3{
        width: 13.5%;
    }
</style>