<template>
    <div>
        <Card>
            <p slot="title">
                排名管理
                <Button type="primary" size="small" @click="refreshPageManual">
                    <Icon type="refresh"></Icon>
                    刷新
                </Button>
            </p>
            <Row class="functionWrapper">
                <div class="searchWrapper">
                    <div class="poptip">
                        手机号：<Input placeholder="手机号" v-model="mobilePhone" />
                        </Input>
                    </div>
                    <div class="poptip">
                        <span>排序类型：</span>
                        <Select v-model="rankType">
                            <Option value="0" key="0">人数排名</Option>
                            <Option value="1" key="1">返佣排名</Option>
                        </Select>
                    </div>
                    <div class="btns">
                        <Button type="info" @click="search">搜索</Button>
                    </div>
                </div>
            </Row>
            <Row class="tableWrapper">
                <Table :columns="columns" :data="list" border :loading="ifLoading">
                </Table>
            </Row>
            <Row class="pageWrapper">
                <Page :total="total" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
            </Row>

        </Card>
        <Modal
          class="auditModel"
          v-model="detailModel"
          title="修改参数"
          @on-ok="alterRankDetail">
          <ul>
            <li><span><i>*</i>ID：</span>
              <p>
                <Input v-model="detailRank.id" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li>
                <span><i>*</i>返佣折合USDT：</span>
                <p> <Input v-model="detailRank.estimatedReward"></Input> </p>
            </li>
            <li>
                <span><i>*</i>额外奖励(USDT)：</span>
                <p> <Input v-model="detailRank.extraReward"></Input> </p>
            </li>
            <li>
                <span><i>*</i>一级邀请人数：</span>
                <p> <Input v-model="detailRank.levelOne"></Input> </p>
            </li>
            <li>
                <span><i>*</i>二级邀请人数：</span>
                <p> <Input v-model="detailRank.levelTwo"></Input> </p>
            </li>
          </ul>
      </Modal>
    </div>
</template>

<script>
import { inviteRank, alterRank } from '@/service/getData';
import expandRow from './SecondRecord';
const getParamsFun = (pageSize) => (pageNo) => (rankType) => (mobilePhone) => ({
    pageNo, pageSize, rankType, mobilePhone
});
const getParams = getParamsFun(20);
export default {
    components: { expandRow },
    data() {
        return {
            list: [],
            total: 0,
            memberId: "",
            rankType: 0,
            pageNo: 1,
            mobilePhone: "",
            email: "",
            currentPageIdx: 1,
            ifLoading: true,
            detailModel: false,
            detailRank: {
                id: "",
                estimatedReward: null,
                extraReward: null,
                levelOne: null,
                levelTwo: null
            },
            columns: [
                {
                    title: "ID",
                    key: "id"
                },
                {
                    title: '用户ID',
                    key: 'memberId'
                },
                {
                    title: '手机号',
                    key: 'userIdentify'
                },
                {
                    title: '一级邀请',
                    key: 'levelOne'
                },
                {
                    title: '二级邀请',
                    key: 'levelTwo'
                },
                {
                    title: '返佣折合',
                    key: 'estimatedReward'
                },
                {
                    title: '额外奖励',
                    key: 'extraReward'
                },
                {
                  title: "操作",
                  key: "age",
                  width: 150,
                  render: (h, obj) => {
                    let params = obj.row;
                    return h("div", [
                      h(
                        "Button",
                        {
                          props: {type: "primary",size: "small"}, style: {marginRight: "10px"},
                          on: {
                            click: () => {
                              this.detailModel = true;
                              this.detailRank.id = params.id;
                              this.detailRank.estimatedReward = params.estimatedReward;
                              this.detailRank.extraReward = params.extraReward;
                              this.detailRank.levelOne = params.levelOne;
                              this.detailRank.levelTwo = params.levelTwo;
                            }
                          }
                        },
                        "详情"
                      )
                    ]);
                  }
                }
            ]
        }
    },
    methods: {
        init() {
            const params = getParams(this.pageNo)(this.rankType)(this.mobilePhone);
            inviteRank(params).then(res => {
                if (!res.code) {
                    this.ifLoading = false;
                    this.list = res.data.content;
                    this.total = res.data.totalElements;
                } else {
                    this.$Message.error(res.message);
                }
            })
        },
        refreshPageManual() {
            this.currentPageIdx = 1;
            this.pageNo = 1;
            this.mobilePhone = "";
            this.init();
        },
        search() {
            this.pageNum = this.currentPageIdx = 1;
            this.init();
        },
        changePage(index) {
            this.pageNo = this.currentPageIdx = index;
            this.init();
        },
        alterRankDetail() {
            let params = {
                id: this.detailRank.id,
                estimatedReward: this.detailRank.estimatedReward,
                extraReward: this.detailRank.extraReward,
                levelOne: this.detailRank.levelOne,
                levelTwo: this.detailRank.levelTwo
            };
            alterRank(params).then(res => {
                if (!res.code) {
                    this.$Message.success("修改成功！");
                    this.refreshPageManual();
                } else {
                    this.$Message.error(res.message);
                }
            })
        }
    },
    created() {
        this.init();
    }
}
</script>

<style lang="less" scoped>
</style>
