<template>
    <Form ref="formInline" :model="formInline" :rules="ruleInline" inline>
        <FormItem prop="coinId">
            <Input type="text" v-model="formInline.coinId" placeholder="coinId">
                <!-- <Icon type="ios-person-outline" slot="prepend"></Icon> -->
            </Input>
        </FormItem>
        <FormItem prop="amount">
            <Input type="text" v-model="formInline.amount" placeholder="amount">
                <!-- <Icon type="ios-locked-outline" slot="prepend"></Icon> -->
            </Input>
        </FormItem>
        <FormItem prop="memberId">
            <Input type="text" v-model="formInline.memberId" placeholder="memberId">
                <!-- <Icon type="ios-locked-outline" slot="prepend"></Icon> -->
            </Input>
        </FormItem>
        <FormItem>
            <Button type="primary" @click="handleSubmit('formInline')">查询</Button>
        </FormItem>
    </Form>
</template>
<script>
import {parnter } from '@/service/getData'
    export default {
        data () {
            return {
                formInline: {
                    coinId: '',
                    amount: '',
                    memberId:''
                },
                ruleInline: {
                    coinId: [
                        { required: true, message: '输入coinId', trigger: 'blur' }
                    ],
                    amount: [
                        { required: true, message: '输入amount', trigger: 'blur' }                       
                    ],
                    memberId:[
                        { required: true, message: '输入memberId', trigger: 'blur' }                       
                    ]
                }
            }
        },
        methods: {
            handleSubmit(name) {
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        this.getdata();
                    } else {
                        this.$Message.error('Fail!');
                    }
                })
            },
            getdata(){
                let param = {
                    coinId:this.formInline.coinId,
                    amount:this.formInline.amount,
                    memberId:this.formInline.memberId
                }
                parnter(param).then(res=>{
                    if(!res.code){
                        this.$Message.success('成功！');
                    }else{
                        this.$Message.error('失败！');
                    }
                })
            }
        }
    }
</script>