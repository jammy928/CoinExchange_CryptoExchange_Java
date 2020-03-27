<style scoped>
    .expand-row{
        margin-bottom: 8px;
    }
    td.ivu-table-expanded-cell{
        padding: 10px 50px;
    }
    .expand-row .ivu-col{
        line-height: 20px;
    }
</style>
<template>
    <div>
        <Row class="expand-row" :class="skin">
            <Col span="8">
                <span class="expand-key">{{$t('exchange.expand.time')}} </span>
            </Col>
            <Col span="4">
                <span class="expand-key">{{$t('exchange.expand.price')}} </span>
            </Col>
            <Col span="4">
                <span class="expand-key">{{$t('exchange.expand.amount')}} </span>
            </Col>
            <Col span="4">
              <span class="expand-key">成交额</span>
            </Col>
            <Col span="4">
                <span class="expand-key">{{$t('exchange.expand.fee')}}</span>
            </Col>
        </Row>
        <Row v-for="(row , index) in rows" key="index">
            <Col span="8">
                <span class="expand-value">{{ row.time|dateFormat }}</span>
            </Col>
            <Col span="4">
                <span class="expand-value">{{ row.price|toFloor}}</span>
            </Col>
            <Col span="4">
                <span class="expand-value">{{ row.amount|toFloor}}</span>
            </Col>
            <Col span="4">
            <span class="expand-value">{{ row.turnover|toFloor}}</span>
            </Col>
            <Col span="4">
                <span class="expand-value">{{ row.fee|toFloor}}</span>
            </Col>
        </Row>
    </div>
</template>
<script>
    export default {
        props: {
            skin:String,
            rows: Array
        },
        created(){
            // console.log(this.skin)
        }
    };
</script>
