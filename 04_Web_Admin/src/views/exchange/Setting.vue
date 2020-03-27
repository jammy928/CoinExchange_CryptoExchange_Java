<template>
 <div>
	 <!-- 待优化 2018年4月16日15:33:35 -->
    <Card>
			<p slot="title">
			  币币设置
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>刷新
        </Button>
			</p>
      <Row class="functionWrapper" style='padding:0px 8px 8px 8px'>
        <Col span="18">
          <div class="searchWrapper" style="float:left;">
              <div class="poptip">
                  <Input placeholder="币种名称" v-model="searchSymbol" /></Input>
              </div>
              <div class="poptip">/</div>
              <div class="poptip">
                  <Input placeholder="基础币种名" v-model="searchBase" /></Input>
              </div>
              <div class="btns">
                  <Button type="info" @click="search">搜索</Button>
              </div>
          </div>
        </Col>
        <Col span="6">
          <div style="float: right" class="clearfix">
            <Button type="error" @click="delcoin" style="display: none;">删除交易对</Button>
            <Button type="primary"@click="addcoin">添加交易对</Button>

          </div>
        </Col>
      </Row>

      <Modal
          class="auditModel"
          v-model="fixModel"
          title="修改信息"
          @on-ok="confirmClicked">
          <ul>
            <li><span><i>*</i> 交易对：</span>
              <p>
                <Input v-model="fixSymbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>

            <li><span><i>*</i> 手续费：</span>
                <p><Input v-model="fee"
                 :class="{'errorFormatBorder': feeClass}"
                  @on-change="checkFee(fee)"></Input>
                </p>
                <em v-show="feeClass">格式不正确</em>
            </li>

            <li><span><i>*</i> 状态：</span>
              <p>
                <RadioGroup v-model="fixEnable">
                  <Radio label="1"><em>启用(上架)</em></Radio>
                  <Radio label="2"><em>禁止(下架)</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 前端显示：</span>
              <p>
                <RadioGroup v-model="fixVisible">
                  <Radio label="1"><em>显示</em></Radio>
                  <Radio label="2"><em>隐藏</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 是否可交易：</span>
              <p>
                <RadioGroup v-model="fixExchangeable">
                  <Radio label="1"><em>是</em></Radio>
                  <Radio label="2"><em>否</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 市价买：</span>
              <p>
                <RadioGroup v-model="fixEnableMarketBuy">
                  <Radio label="1"><em>可以</em></Radio>
                  <Radio label="0"><em>不可以</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 市价卖：</span>
              <p>
                <RadioGroup v-model="fixEnableMarketSell">
                  <Radio label="1"><em>可以</em></Radio>
                  <Radio label="0"><em>不可以</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li><span><i>*</i> 排序：</span>
              <p><Input v-model="fixSort"
                        :class="{'errorFormatBorder': sortClass}"
                        @on-change="checksort(fixSort)" placeholder="例：5，须大于4(必须)"></Input>
              </p>
              <em v-show="sortClass">格式不正确</em>
            </li>

            <li><span><i>*</i> 最高买单价：</span>
                <p><Input v-model="fixMaxBuyPrice"></Input>
                </p>
            </li>
            <li><span><i>*</i> 最小下单额：</span>
                <p><Input v-model="fixMinTurnover"></Input>
                </p>
            </li>
          </ul>

      </Modal>
      <Modal
          class="auditModel"
          v-model="auditModal"
          title="添加币种"
          width="850"
          @on-ok="confirmAudit">
          <Row :gutter="30">
            <Col span="10">
              <p class="setting-title">基本信息设置</p>
              <ul>
                <li><span><i>*</i> 交易对：</span>
                  <p>
                    <Input v-model="symbol"
                           :class="{'errorFormatBorder': checkSymbolClass}"
                           @on-change="checkPair(symbol)" placeholder="例: BTC/USDT(必须)"></Input>
                  </p>
                  <em v-show="checkSymbolClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 交易币种：</span>
                  <p><Input v-model="coinSymbol"
                            :class="{'errorFormatBorder': coinSymbolClass}"
                            @on-change="checkCoinSymbol(coinSymbol)"  placeholder="例: BTC(必须)"></Input>
                  </p>
                  <em v-show="coinSymbolClass">格式不正确</em>
                </li>

                <li><span><i>*</i> 结算币种：</span>
                  <p><Input v-model="baseSymbol"
                            :class="{'errorFormatBorder': baseSymbolClass}"
                            @on-change="checkbaseSymbol(baseSymbol)" placeholder="例: USDT(必须)"></Input>
                  </p>
                   <em v-show="baseSymbolClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 手续费：</span>
                  <p><Input v-model="fee"
                   :class="{'errorFormatBorder': feeClass}"
                    @on-change="checkFee(fee)" placeholder="例: 0.001(必须)"></Input>
                  </p>
                  <em v-show="feeClass">格式不正确</em>
                </li>

                <li><span><i>*</i> 币种精度：</span>
                  <p><Input v-model="coinScale"
                            :class="{'errorFormatBorder': coinScaleClass}"
                            @on-change="checkCoinScale(coinScale)" placeholder="例: 4(必须)"></Input>
                  </p>
                  <em v-show="coinScaleClass">格式不正确</em>

                </li>

                <li><span><i>*</i> 基币小数精度：</span>
                  <p><Input v-model="baseCoinScale"
                            :class="{'errorFormatBorder': baseCoinScaleClass}"
                            @on-change="checkbaseCoinScale(baseCoinScale)" placeholder="例: 6(必须)"></Input>
                  </p>
                  <em v-show="baseCoinScaleClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最低卖单价：</span>
                  <p><Input v-model="minSellPrice"
                            :class="{'errorFormatBorder': minSellPriceClass}"
                            @on-change="checkminSellPrice(minSellPrice)" placeholder="不限制: 0.00000000(必须)"></Input>
                  </p>
                  <em v-show="minSellPriceClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最高买单价：</span>
                  <p><Input v-model="maxBuyPrice"
                            :class="{'errorFormatBorder': maxBuyPriceClass}"
                            @on-change="checkmaxBuyPrice(maxBuyPrice)" placeholder="不限制: 0.00000000(必须)"></Input>
                  </p>
                  <em v-show="maxBuyPriceClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最小下单量：</span>
                  <p><Input v-model="minVolume"
                            :class="{'errorFormatBorder': minVolumeClass}"
                            @on-change="checkminVolume(minVolume)" placeholder="不限制: 0.00000000(必须)"></Input>
                  </p>
                  <em v-show="minVolumeClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 最大下单量：</span>
                  <p><Input v-model="maxVolume"
                            :class="{'errorFormatBorder': maxVolumeClass}"
                            @on-change="checkmaxVolume(maxVolume)" placeholder="不限制: 0.00000000(必须)"></Input>
                  </p>
                  <em v-show="maxVolumeClass">格式不正确</em>
                </li>
                <li><span><i>*</i> 交易区：</span>
                  <p><Input v-model="zone"
                            :class="{'errorFormatBorder': zoneClass}"
                            @on-change="checkzone(zone)" placeholder="0:主板, 1:创新板(必须)"></Input>
                  </p>
                  <em v-show="zoneClass">格式不正确</em>
                </li>
                <li><span><i>*</i>最小挂单额：</span>
                  <p><Input v-model="minTurnover"
                            :class="{'errorFormatBorder': minTurnoverClass}"
                            @on-change="checkminTurnover(minTurnover)" placeholder="不限制: 0.00000000(必须)"></Input>
                  </p>
                  <em v-show="minTurnoverClass">格式不正确</em>
                </li>
                <li><span><i>*</i>排序：</span>
                  <p><Input v-model="sort"
                            :class="{'errorFormatBorder': sortClass}"
                            @on-change="checksort(sort)" placeholder="例：5，须大于4(必须)"></Input>
                  </p>
                  <em v-show="sortClass">格式不正确</em>
                </li>
              </ul>
            </Col>
            <Col span="12">
              <Row :gutter="30">
                <p class="setting-title">币种活动设置</p>
                <ul>
                  <li><span><i>*</i>活动类型：</span>
                    <p>
                      <RadioGroup v-model="publishType">
                        <Radio label="1"><em>无活动</em></Radio>
                        <Radio label="2"><em>抢购发行</em></Radio>
                        <Radio label="3"><em>分摊发行</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span>活动开始时间：</span>
                    <p>
                      <DatePicker v-model="startTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>抢购发行与分摊发行都需要设置</em>
                  </li>
                  <li><span>活动结束时间：</span>
                    <p>
                      <DatePicker v-model="endTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>抢购发行与分摊发行都需要设置</em>
                  </li>
                  <li><span>清盘结束时间：</span>
                    <p>
                      <DatePicker v-model="clearTime" type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="yyyy-MM-dd HH:mm:ss" style="width: 200px"></DatePicker>
                    </p>
                    <em>抢购发行与分摊发行都需要设置</em>
                  </li>
                  <li><span>分摊发行价：</span>
                    <p><Input v-model="publishPrice"
                               style="width: 200px"
                              :class="{'errorFormatBorder': publishPriceClass}"
                              @on-change="checkpublishPrice(publishPrice)" placeholder="不限制: 0.00000000(必须)"></Input>
                    </p>
                    <em>抢购发行与分摊发行都需要设置</em>
                  </li>
                  <li><span>发行数量：</span>
                    <p><Input v-model="publishAmount"
                               style="width: 200px" placeholder="不限制: 10000.00000000(必须)"></Input>
                    </p>
                    <em>抢购发行与分摊发行都需要设置</em>
                  </li>
                </ul>
              </Row>
              <Row :gutter="30">
                <p class="setting-title">币种状态设置</p>
                <ul>
                  <li><span><i>*</i> 状态：</span>
                    <p>
                      <RadioGroup v-model="enable">
                        <Radio label="1"><em>启用</em></Radio>
                        <Radio label="2"><em>禁止</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> 前端显示：</span>
                    <p>
                      <RadioGroup v-model="visible">
                        <Radio label="1"><em>显示</em></Radio>
                        <Radio label="2"><em>隐藏</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> 是否可交易：</span>
                    <p>
                      <RadioGroup v-model="exchangeable">
                        <Radio label="1"><em>是</em></Radio>
                        <Radio label="2"><em>否</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> 市价买：</span>
                    <p>
                      <RadioGroup v-model="enableMarketBuy">
                        <Radio label="1"><em>可以</em></Radio>
                        <Radio label="0"><em>不可以</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> 市价卖：</span>
                    <p>
                      <RadioGroup v-model="enableMarketSell">
                        <Radio label="1"><em>可以</em></Radio>
                        <Radio label="0"><em>不可以</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                  <li><span><i>*</i> 机器人类型：</span>
                    <p>
                      <RadioGroup v-model="robotType">
                        <Radio label="0"><em>一般</em></Radio>
                        <Radio label="1"><em>定价</em></Radio>
                        <Radio label="2"><em>控盘</em></Radio>
                      </RadioGroup>
                    </p>
                  </li>
                </ul>
              </Row>
            </Col>
          </Row>
      </Modal>

      <Table
        border
        :columns="columns_first"
        :data="exchange"
        @on-selection-change="selected"
        :loading="ifLoading">
      </Table>

      <Row class="pageWrapper">
        <Page
        :total="pageNum"
        class="buttomPage"
        :current="current"
        @on-change="changePage"
        show-elevator></Page>
      </Row>
      <p style="font-size:11px;">注意1：【状态】= 下架，无论【可交易】的状态为是或否，都无法交易，并且前台也不显示</p>
      <p style="font-size:11px;">注意2：【状态】= 上架，且【可交易】= 是，可以正常交易</p>
      <p style="font-size:11px;">注意3：【状态】= 上架，且【可交易】= 否，不可以交易</p>
      <p style="font-size:11px;">注意4：【状态】= 上架，且【显示】= 是，前台显示</p>
      <p style="font-size:11px;">注意5：【状态】用于整体控制交易对，除非下架交易对，否则都应该一直设置为“上架”状态</p>
      <p style="font-size:11px;">注意6：【可交易】用于控制上架状态的交易对是否可交易，这种情形一般应用于暂停交易或维护交易对时</p>
      <p style="font-size:11px;">注意7：【显示】用于控制上架状态的交易对是否在前天显示，一般应用于交易对上线前的一些准备工作，如启动交易引擎&行情引擎等</p>
      <p style="font-size:11px;">注意8：启动引擎前，交易对状态需要设置：【状态】= 上架</p>
      <p style="font-size:11px;">注意9：停止引擎前，交易对状态需要设置：【可交易】= 是</p>
      <p style="font-size:11px;">注意10：撤销所有委托前，须在【交易引擎】和【行情引擎】状态同时为“运行中”，且须设置【可交易】= 否，并且等待几分钟确保积累订单被处理完（可通过查看交易引擎委托状态订单数变化进行判断）</p>
      <p style="font-size:11px;">注意11：新建币种时，【显示】设置为隐藏，【可交易】设置为禁用，然后开启引擎后，等待行情引擎启动后，再将【显示】设置为可见。否则会导致前端无法获取交易列表</p>
      <hr>
      <p style="font-size:11px;">外部交易所行情获取：http://118.25.133.182:10000/ermarket/thumb/xmrusdt</p>
      <p style="font-size:11px;">获取所有机器人配置：http://118.25.133.182:20000/ernormal/robotList</p>
      <Modal
        v-model="ifDelete"
        title="删除交易对"
        @on-ok="confirmDel"
        @on-cancel="$Message.info('已取消！')">
        <p>是否删除选中的{{ deleteArr.length }}项</p>
       </Modal>
			 <Modal
					class="auditModel"
					v-model="loginPassModal"
					title="请输入登录密码"
					width="350"
					@on-cancle="loginPW = ''"
					@on-ok="confirmLoginPass">
					<Input v-model="loginPW" type="password" placeholder="请输入登录密码"></Input>
			 </Modal>

       <Modal
          class="auditModel"
          v-model="startEngineModel"
          title="启动撮合交易引擎确认"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">启动撮合交易：<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">确定启动该交易对的撮合交易引擎吗?</p>
       </Modal>

       <Modal
          class="auditModel"
          v-model="stopEngineModel"
          title="停止撮合交易引擎确认"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">停止撮合交易：<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">确定停止该交易对的撮合交易引擎吗?</p>
       </Modal>
       <Modal
          class="auditModel"
          v-model="cancelAllModel"
          title="撤销所有委托确认"
          width="350"
          @on-cancle="$Message.info('已取消！')"
          @on-ok="confirmClicked">
          <p style="font-size:20px;font-weight:bold;text-align:center;">撤销所有委托：<span style="color: #FF0000;">{{targetSymbol}}</span></p>
          <p style="font-size:12px;text-align:center;margin-top: 10px;">确定撤销该交易对所有委托订单吗?</p>
       </Modal>

       <Modal
          class="auditModel"
          v-model="exchangeEngineModel"
          title="交易引擎委托明细"
          width="350"
          @on-cancle="confirmExchangeEngineMoel"
          @on-ok="">
          <p style="font-size:20px;font-weight:bold;text-align:center;margin-bottom:20px;"><span style="color: #FF0000;">{{engineTargetSymbol}}</span></p>
          <Row :gutter="30">
            <Col span="12">
              <p style="font-size:14px;font-weight:bold;text-align:center;margin-bottom: 10px;padding-bottom:10px;border-bottom: 1px solid #B7B7B7;"><span>买单概况</span></p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">限价买单：{{engineLimitBuyCount}}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">市价买单：{{engineMarketBuyCount}}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">买单深度：{{engineBuyDepth}}</p>
            </Col>
            <Col span="12">
              <p style="font-size:14px;font-weight:bold;text-align:center;margin-bottom: 10px;padding-bottom:10px;border-bottom: 1px solid #B7B7B7;"><span>卖单概况</span></p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">限价卖单：{{engineLimitSellCount}}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">限价卖单：{{engineMarketSellCount}}</p>
              <p style="font-size:12px;text-align:center;margin-top: 10px;">卖单深度：{{engineSellDepth}}</p>
            </Col>
          </Row>
       </Modal>
       <!-- 一般机器人 -->
       <Modal
          class="auditModel"
          v-model="robotModel"
          title="（一般机器人）修改机器人参数"
          @on-ok="alterRobotClicked">
          <ul>
            <li><span><i>*</i>交易对：</span>
              <p>
                <Input v-model="robotParams.symbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li><span><i>*</i> 启动机器人：</span>
              <p>
                <RadioGroup v-model="robotParams.isHalt">
                  <Radio label="1"><em>停止</em></Radio>
                  <Radio label="0"><em>启动</em></Radio>
                </RadioGroup>
              </p>
            </li>
            <li>
                <span><i>*</i>最低交易量：</span>
                <p> <Input v-model="robotParams.startAmount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(1%)：</span>
                <p> <Input v-model="robotParams.randRange0"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(9%)：</span>
                <p> <Input v-model="robotParams.randRange1"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="robotParams.randRange2"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="robotParams.randRange3"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="robotParams.randRange4"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="robotParams.randRange5"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(10%)：</span>
                <p> <Input v-model="robotParams.randRange6"></Input> </p>
            </li>
            <li>
                <span><i>*</i>价格精度要求：</span>
                <p> <Input v-model="robotParams.scale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>数量精度要求：</span>
                <p> <Input v-model="robotParams.amountScale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>买卖盘最高差价：</span>
                <p> <Input v-model="robotParams.maxSubPrice"></Input> </p>
            </li>
            <li>
                <span><i>*</i>初始订单数(>24)：</span>
                <p> <Input v-model="robotParams.initOrderCount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>价格变化步长(%)：</span>
                <p> <Input v-model="robotParams.priceStepRate"></Input> </p>
            </li>
            <li>
                <span><i>*</i>下单时间间隔(毫秒)：</span>
                <p> <Input v-model="robotParams.runTime"></Input> </p>
            </li>
          </ul>
      </Modal>

      <!-- 固定价格机器人 -->
      <Modal
          class="auditModel"
          v-model="priceRobotModel"
          title="（定价机器人）修改机器人参数"
          @on-ok="alterPriceRobotClicked">
          <ul>
            <li><span><i>*</i>交易对：</span>
              <p>
                <Input v-model="priceRobotParams.symbol" disabled></Input>
                <span>{{ }}</span>
              </p>
            </li>
            <li><span><i>*</i> 启动机器人：</span>
              <p>
                <RadioGroup v-model="priceRobotParams.isHalt">
                  <Radio label="1"><em>停止</em></Radio>
                  <Radio label="0"><em>启动</em></Radio>
                </RadioGroup>
              </p>
            </li>

            <li>
                <span><i>*</i>固定价格：</span>
                <p> <Input v-model="priceRobotParams.price"></Input> </p>
            </li>
            <li>
                <span><i>*</i>最低交易量：</span>
                <p> <Input v-model="priceRobotParams.startAmount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(1%)：</span>
                <p> <Input v-model="priceRobotParams.randRange0"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(9%)：</span>
                <p> <Input v-model="priceRobotParams.randRange1"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="priceRobotParams.randRange2"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="priceRobotParams.randRange3"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="priceRobotParams.randRange4"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(20%)：</span>
                <p> <Input v-model="priceRobotParams.randRange5"></Input> </p>
            </li>
            <li>
                <span><i>*</i>交易量随机因子(10%)：</span>
                <p> <Input v-model="priceRobotParams.randRange6"></Input> </p>
            </li>
            <li>
                <span><i>*</i>价格精度要求：</span>
                <p> <Input v-model="priceRobotParams.scale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>数量精度要求：</span>
                <p> <Input v-model="priceRobotParams.amountScale"></Input> </p>
            </li>
            <li>
                <span><i>*</i>买卖盘最高差价：</span>
                <p> <Input v-model="priceRobotParams.maxSubPrice"></Input> </p>
            </li>
            <li>
                <span><i>*</i>初始订单数(>24)：</span>
                <p> <Input v-model="priceRobotParams.initOrderCount"></Input> </p>
            </li>
            <li>
                <span><i>*</i>价格变化步长(%)：</span>
                <p> <Input v-model="priceRobotParams.priceStepRate"></Input> </p>
            </li>
            <li>
                <span><i>*</i>下单时间间隔(毫秒)：</span>
                <p> <Input v-model="priceRobotParams.runTime"></Input> </p>
            </li>
          </ul>
      </Modal>
    </Card>
 </div>
</template>

 <script>
import dtime from 'time-formater'
import { transactionPair } from '@/caculate/caculate'
import { delBBSetting, addBBSetting, queryBBSetting, fixBBSetting, startBBTrader, stopBBTrader, cancelBBAllOrders, overviewBB, getRobotConfig, setRobotConfig, createRobotConfig, createPriceRobotConfig, setPriceRobotConfig} from '@/service/getData'

export default {
  data() {
    return {
			loginPW: '',
			loginPassModal: false,
      ifLoading: true,
      // currentPageIdx: 1,
      coinScaleClass: false,
      baseSymbolClass: false,
      checkSymbolClass: false,
      coinSymbolClass: false,
      feeClass: false,
      baseCoinScaleClass: false,
      minVolumeClass: false,
      maxVolumeClass: false,
      zoneClass: false,
      minSellPriceClass: false,
      maxBuyPriceClass: false,
      minTurnoverClass: false,
      sortClass: false,
      publishPriceClass: false,

      operation: 1, // 1: 设置   2：启动引擎   3：停止引擎   4：撤销委托

      fixSymbol: null,
      fixEnable: 1,
      fixModel: false,
      fixSort: null,
      fixVisible: 1,
      fixExchangeable: 1,
      fixEnableMarketSell: "1",
      fixEnableMarketBuy: "1",
      fixMaxBuyPrice: null,
      fixMinTurnover: null,

      startEngineModel: false,
      stopEngineModel: false,
      cancelAllModel: false,
      targetSymbol: null,

      searchSymbol: "",
      searchBase: "",

      robotModel: false,// 机器人参数
      robotMode: 0, // 0:新建  1：修改
      robotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0,
        randRange0: 0,
        randRange1: 0,
        randRange2: 0,
        randRange3: 0,
        randRange4: 0,
        randRange5: 0,
        randRange6: 0,
        scale: 0,
        amountScale: 0,
        maxSubPrice: 0,
        initOrderCount: 0,
        priceStepRate: 0,
        runTime: 0
      },
      defaultRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0.001,
        randRange0: 50,
        randRange1: 20,
        randRange2: 10,
        randRange3: 5,
        randRange4: 1,
        randRange5: 0.1,
        randRange6: 0.01,
        scale: 4,
        amountScale: 6,
        maxSubPrice: 1,
        initOrderCount: 30,
        priceStepRate: 0.005,
        runTime: 20000
      },
      priceRobotModel: false,// 机器人参数
      priceRobotMode: 0, // 0:新建  1：修改
      priceRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0,
        randRange0: 0,
        randRange1: 0,
        randRange2: 0,
        randRange3: 0,
        randRange4: 0,
        randRange5: 0,
        randRange6: 0,
        scale: 0,
        price: 0,
        amountScale: 0,
        maxSubPrice: 0,
        initOrderCount: 0,
        priceStepRate: 0,
        runTime: 0
      },
      defaultPriceRobotParams: {
        symbol: "",
        isHalt: "1",
        startAmount: 0.001,
        randRange0: 50,
        randRange1: 20,
        randRange2: 10,
        randRange3: 5,
        randRange4: 1,
        randRange5: 0.1,
        randRange6: 0.01,
        scale: 4,
        amountScale: 6,
        price: 1,
        maxSubPrice: 1,
        initOrderCount: 30,
        priceStepRate: 0.005,
        runTime: 20000
      },
      symbol: null,
      coinSymbol: null,
      baseSymbol: null,
      enable: "1",
      fee: null,
      coinScale: null,
      baseCoinScale: null,
      minVolume: null, // add
      maxVolume: null, // add
      zone: null, // add
      minSellPrice: null, // add
      maxBuyPrice: null, // add
      minTurnover: null, // add
      sort: null, // add
      publishPrice: null, // add  分摊抢购初始价格
      publishType: "1", // 发行活动：1-无活动  2-抢购   3-分摊
      publishAmount: 0,
      startTime: null,
      endTime: null,
      clearTime: null,
      visible: "1",
      exchangeable: "1",
      robotType: "0",
      enableMarketBuy: "1",
      enableMarketSell: "1",

      auditModal: false,
      pageNum: null,
      current: 1,
      deleteArr: false,
      ifDelete: false,

      exchangeEngineModel: false,
      engineTargetSymbol: "",
      engineLimitSellCount: "",
      engineLimitBuyCount: "",
      engineMarketSellCount: "",
      engineMarketBuyCount: "",
      engineBuyDepth: 0,
      engineSellDepth: 0,
      columns_first: [
        {
          type: "selection",
          width: 55,
          align: 'center'
        },
        {
          title: "#",
          width: 50,
          type: "index"
        },
        {
          title: "交易对",
          width: 120,
          key: "symbol",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "left"
                }
              }, [
                h("span", {style:{fontWeight:"bold"}}, row.symbol)
              ]);
          }
        },
        {
          title: "市价买卖",
          width: 65,
          key: "fee",
          render: (h, params) => {
            const row = params.row;
            const marketBuyText = row.enableMarketBuy == 1 ? "☑" : "✖";
            const marketSellText = row.enableMarketSell == 1 ? "☑" : "✖";
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {style:{fontSize:"10px"}}, "" + marketBuyText + " / " + marketSellText)
              ]);
          }
        },
        {
          title: "显示",
          width:45,
          key: "visible",
          render: (h, params) => {
            const row = params.row;
            const visible = row.visible == "1" ? "是" : "否";
            if(row.visible == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, visible);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, visible);
          }
        },
        {
          title: "可交易",
          width: 45,
          key: "exchangeable",
          render: (h, params) => {
            const row = params.row;
            const exchangeable = row.exchangeable == "1" ? "是" : "否";
            if(row.exchangeable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, exchangeable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, exchangeable);
          }
        },
        {
          title: "机器人",
          width: 60,
          key: "robotType",
          render: (h, params) => {
            const row = params.row;
            var robot = "一般";
            if(row.robotType == "1"){
              robot = "定价";
            }
            if(row.robotType == "2"){
              robot = "控盘";
            }
            return h("span", {
              style:{
                color:'#42b983'
              }
            }, robot);
          }
        },{
          title: "机器人参数",
          width: 80,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var btnType = row.exEngineStatus == 1 ? "primary" : "default";
            var btnText = row.exEngineStatus == 1 ? "设置" : "新建";
            return h("div", [
              h(
                "Button",
                  {
                    props: {type: btnType, size: "small"},
                    style: {marginRight: "5px"},
                    on: {
                      click: () => {
                        this.$Loading.start();
                        if(row.robotType == 0){
                          this.robotMode = row.exEngineStatus; // 0:创建   1:修改
                        }else if(row.robotType == 1) {
                          this.priceRobotMode = row.exEngineStatus; // 0:创建   1:修改
                        }
                        let obj ={
                          symbol:  row.symbol
                        }
                        getRobotConfig(obj).then( res => {
                          // 一般机器人
                          if (!res.code) {
                            if(row.robotType == 0){
                              this.robotParams.symbol = row.symbol;
                              if(res.data.halt) {
                                this.robotParams.isHalt = "1";
                              }else{
                                this.robotParams.isHalt = "0";
                              }
                              this.robotParams.startAmount = res.data.startAmount;
                              this.robotParams.randRange0 = res.data.randRange0;
                              this.robotParams.randRange1 = res.data.randRange1;
                              this.robotParams.randRange2 = res.data.randRange2;
                              this.robotParams.randRange3 = res.data.randRange3;
                              this.robotParams.randRange4 = res.data.randRange4;
                              this.robotParams.randRange5 = res.data.randRange5;
                              this.robotParams.randRange6 = res.data.randRange6;
                              this.robotParams.scale = res.data.scale;
                              this.robotParams.amountScale = res.data.amountScale;
                              this.robotParams.maxSubPrice = res.data.maxSubPrice;
                              this.robotParams.initOrderCount = res.data.initOrderCount;
                              this.robotParams.priceStepRate = res.data.priceStepRate;
                              this.robotParams.runTime = res.data.runTime;

                              this.robotModel = true;
                              this.$Loading.finish();
                            }
                            // 固定价格机器人
                            if(row.robotType == 1){
                              this.priceRobotParams.symbol = row.symbol;
                              if(res.data.halt) {
                                this.priceRobotParams.isHalt = "1";
                              }else{
                                this.priceRobotParams.isHalt = "0";
                              }
                              this.priceRobotParams.startAmount = res.data.startAmount;
                              this.priceRobotParams.randRange0 = res.data.randRange0;
                              this.priceRobotParams.randRange1 = res.data.randRange1;
                              this.priceRobotParams.randRange2 = res.data.randRange2;
                              this.priceRobotParams.randRange3 = res.data.randRange3;
                              this.priceRobotParams.randRange4 = res.data.randRange4;
                              this.priceRobotParams.randRange5 = res.data.randRange5;
                              this.priceRobotParams.randRange6 = res.data.randRange6;
                              this.priceRobotParams.scale = res.data.scale;
                              this.priceRobotParams.amountScale = res.data.amountScale;
                              this.priceRobotParams.price = res.data.price;
                              this.priceRobotParams.maxSubPrice = res.data.maxSubPrice;
                              this.priceRobotParams.initOrderCount = res.data.initOrderCount;
                              this.priceRobotParams.priceStepRate = res.data.priceStepRate;
                              this.priceRobotParams.runTime = res.data.runTime;

                              this.priceRobotModel = true;
                              this.$Loading.finish();
                            }
                          }else{
                            if(row.robotType == 0){
                              this.robotParams = this.defaultRobotParams;
                              this.robotParams.symbol = row.symbol;
                              this.robotModel = true;
                              this.$Notice.info({
                                    title: "创建机器人",
                                    desc: "该交易对没有机器人",
                                    duration: 10
                                });
                              this.$Loading.finish();
                            }
                            if(row.robotType == 1){
                              this.priceRobotParams = this.defaultPriceRobotParams;
                              this.priceRobotParams.symbol = row.symbol;
                              this.priceRobotModel = true;
                              this.$Notice.info({
                                    title: "创建机器人",
                                    desc: "该交易对没有机器人",
                                    duration: 10
                                });
                              this.$Loading.finish();
                            }
                          }
                        });
                      }
                    }
                  },
                  btnText
                )
            ]);
          }
        },
        {
          title: "交易引擎",
          width: 100,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var engineStatus = "无引擎";
            if(row.engineStatus == "1"){
              engineStatus = "运行中";
            }
            if(row.engineStatus == "2"){
              engineStatus = "暂停中";
            }
            if(row.engineStatus == "1") {
              return h(
                "Button",
                  {
                    props: {type: "success",size: "small", icon:"ios-information-outline"},
                    style: {marginRight: "5px"},
                    on: {
                      click: () => {
                        this.$Loading.start();

                        this.engineLimitBuyCount = "-";
                        this.engineMarketBuyCount = "-";
                        this.engineLimitSellCount = "-";
                        this.engineMarketSellCount = "-";
                        this.engineBuyDepth = "-";
                        this.engineSellDepth = "-";

                        this.exchangeEngineModel = true;
                        this.engineTargetSymbol = params.row.symbol;

                        let obj ={
                          symbol:  this.engineTargetSymbol
                        }
                        overviewBB(obj).then( res => {
                          if (!res.code) {

                            this.engineLimitBuyCount = res.data.bid.limit_price_order_count;
                            this.engineMarketBuyCount = res.data.bid.market_price_order_count;
                            this.engineLimitSellCount = res.data.ask.limit_price_order_count;
                            this.engineMarketSellCount = res.data.ask.market_price_order_count;
                            this.engineBuyDepth = res.data.bid.depth;
                            this.engineSellDepth = res.data.ask.depth;

                            this.$Loading.finish();
                          }else{
                            this.$Notice.error({
                                  title: "获取失败",
                                  desc: res.message,
                                  duration: 10
                              });
                            this.$Loading.error();
                          }
                        });
                      }
                    }
                  },
                  engineStatus
                );
            }
            return h("div", {
              style:{
                  color:'#FF0000',
                  borderRadius: "4px",
                  border: "1px solid #FF0000",
                  padding: "2px 4px",
                  fontSize:"11px"
                }
            }, engineStatus);
          }
        },
        {
          title: "行情引擎",
          width: 100,
          key: "engineStatus",
          render: (h, params) => {
            const row = params.row;
            var marketEngineStatus = "无引擎";
            if(row.marketEngineStatus == "1"){
              marketEngineStatus = "运行中";
            }
            if(row.marketEngineStatus == "2"){
              marketEngineStatus = "暂停中";
            }

            if(row.marketEngineStatus == "1" && row.engineStatus == "2") {
              return h("div", [
              h(
                "Button",
                {
                  props: {type: "error",size: "small", loading:true},
                  style: {marginRight: "5px"},
                },
                "停止中"
              )]);
            }

            if(row.marketEngineStatus == "1") {
              return h("div", {
                    style:{
                      color:'#19be6b',
                      borderRadius: "4px",
                      border: "1px solid #19be6b",
                      padding: "2px 8px",
                      fontSize:"11px",
                      display: "inline-block"
                    }
                  },
                  marketEngineStatus
                );
            }

            if(row.marketEngineStatus == "2" && row.engineStatus == "1"){
              return h("div", [
              h(
                "Button",
                {
                  props: {type: "success",size: "small", loading:true},
                  style: {marginRight: "5px"},
                },
                "启动中"
              )]);
            }
            return h("div", {
              style:{
                  color:'#FF0000',
                  borderRadius: "4px",
                  border: "1px solid #FF0000",
                  padding: "2px 8px",
                  fontSize:"11px",
                  display: "inline-block"
                }
            }, marketEngineStatus);
          }
        },
        {
          title: "状态",
          width: 70,
          key: "enable",
          render: (h, params) => {
            const row = params.row;
            const enable = row.enable == "1" ? "上架" : "下架";
            if(row.enable == "1") {
              return h("span", {
                style:{
                  color:'#42b983'
                }
              }, enable);
            }
            return h("span", {
              style:{
                  color:'#FF0000'
                }
            }, enable);
          }
        },
        {
          title: '排序',
          key:"sort",
          width: 60
        },
        {
          title: "发行活动",
          width: 150,
          key: "publishType",
          render: (h, params) => {
            const row = params.row;
            const currentTime = dtime(row.currentTime).format('YYYY-MM-DD HH:mm:ss');
            var publishType = "未知";
            const txtColor = currentTime > row.clearTime ? "#B7B7B7" : "#42b983";

            if(row.publishType == "NONE"){
              publishType = "无活动";
            }
            if(row.publishType == "QIANGGOU"){
              publishType = currentTime > row.clearTime ? "抢购(已结束)" : "抢购(进行中)";
            }
            if(row.publishType == "FENTAN"){
              publishType = currentTime > row.clearTime ? "分摊(已结束)" : "分摊(进行中)";
            }

            if(row.publishType == "QIANGGOU" || row.publishType == "FENTAN") {
                if(currentTime > row.clearTime){
                    return h("div", {
                      style:{
                        color: txtColor,
                        textAlign: "center"
                      }
                    }, [
                      h("span", {}, publishType)
                    ]);
                }else{
                    return h("div", {
                      style:{
                        color: txtColor,
                        textAlign: "left"
                      }
                    }, [
                      h("span", {}, publishType),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, "开始："+params.row.startTime.substr(0, 16)),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, "结束："+params.row.endTime.substr(0, 16)),
                      h("br", {}, ""),
                      h("span", {style:{fontSize:"8px"}}, "盘整："+params.row.clearTime.substr(0, 16))
                    ]);
                }
            }
            return h("span", {
              style:{
                  color:'#b7b7b7'
                }
            }, publishType);
          }
        },
        {
          title: "手续费",
          width: 80,
          key: "symbol",
          render: (h, params) => {
            const row = params.row;
            return h("div", {
                style:{
                  textAlign: "center"
                }
              }, [
                h("span", {}, (row.fee * 1000 + "‰"))
              ]);
          }
        },
        {
          title: "推荐",
          key: "flag",
          width: 60,
          render: (h, params) => {
            const row = params.row;
            const aFlag = row.flag === 1 ? "√" : "×";
            return h("span", {}, aFlag);
          }
        },
        {
          title: '币种精度',
          key:"coinScale",
          width: 90
        },
        {
          title: '基币精度',
          key:"baseCoinScale",
          width: 90
        },
        {
          title: '最小成交额',
          key:"minTurnover",
          width: 100
        },
        {
          title: '最小下单量',
          key:"minVolume",
          width: 100
        },
        {
          title: '最大下单量',
          key:"maxVolume",
          width: 100
        },
        {
          title: '卖单最低价',
          key:"minSellPrice",
          width: 100
        },
        {
          title: '买单最高价',
          key:"maxBuyPrice",
          width: 100
        },
        {
          title: "操作",
          key: "xx",
          width: 250,
          fixed: 'right',
          render: (h, obj) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {type: "info",size: "small",icon:"android-settings"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      this.operation = 1;
                      this.fixModel = true;
                      this.fixSymbol = obj.row.symbol;
                      this.fee = obj.row.fee;
                      this.fixMaxBuyPrice = obj.row.maxBuyPrice;
                      this.fixMinTurnover = obj.row.minTurnover;
                      this.fixSort = obj.row.sort;
                      this.fixEnable = String(obj.row.enable);
                      this.fixVisible = String(obj.row.visible);
                      this.fixExchangeable = String(obj.row.exchangeable);
                      this.fixEnableMarketBuy = String(obj.row.enableMarketBuy);
                      this.fixEnableMarketSell = String(obj.row.enableMarketSell);
                    }
                  }
                },
                "设置"
              ),
              h(
                "Button",
                {
                  props: {type: "success",size: "small", icon:"ios-play"},
                  style: {marginRight: "5px", display: obj.row.engineStatus == "1" ? "none" : "inline-block"},
                  on: {
                    click: () => {
                      this.operation = 2;
                      this.startEngineModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  },
                  key: 'loading'
                },
                "启动引擎"
              ),
              h(
                "Button",
                {
                  props: {type: "error",size: "small", icon:"stop"},
                  style: {marginRight: "5px", display: obj.row.engineStatus != "1" ? "none" : "inline-block"},
                  on: {
                    click: () => {
                      this.operation = 3;
                      this.stopEngineModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  }
                },
                "停止引擎"
              ),
              h(
                "Button",
                {
                  props: {type: "warning",size: "small"},
                  style: {marginRight: "5px"},
                  on: {
                    click: () => {
                      if(obj.row.engineStatus != "1"){
                        this.$Notice.error({
                            title: "撤销所有委托失败",
                            desc: "请在交易引擎状态为【运行中】时撤销委托。",
                            duration: 10
                        });
                        return;
                      }
                      if(obj.row.exchangeable == "1"){
                        this.$Notice.error({
                            title: "撤销所有委托失败",
                            desc: "请先设置交易对的【可交易】状态为否，然后等待一分钟再操作",
                            duration: 10
                        });
                        return;
                      }
                      this.operation = 4;
                      this.cancelAllModel = true;
                      this.targetSymbol = obj.row.symbol;
                    }
                  }
                },
                "撤销所有委托"
              )
            ]);
          }
        }
      ],
      exchange: []
    };
  },
  methods: {
    checkbaseCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.baseCoinScaleClass =  bol;
    },
    checkCoinScale(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.coinScaleClass =  bol;
    },
    checkFee(str) {
      let re = /\d|\d+.\d+$/g;
      this.feeClass = !re.test(String(str)) || !(str*1>0);
    },
    checkbaseSymbol(str) {
      let re = /[^A-Z]/g;
      this.baseSymbolClass =  re.test(str);
    },
    checkCoinSymbol(str) {
      let re = /[^A-Z]/g;
      this.coinSymbolClass =  re.test(str);
    },
    checkPair(str) {
     let re = /^[A-Z]+\/[A-Z]+$/g;
      this.checkSymbolClass = !re.test(str);
      console.log(this.checkSymbolClass);
		},
    checkminVolume(str) {
      let re = /\d|\d+.\d+$/g;
      this.minVolumeClass = !re.test(String(str));
    },
    checkmaxVolume(str) {
      let re = /\d|\d+.\d+$/g;
      this.maxVolumeClass = !re.test(String(str));
    },
    checkzone(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.zoneClass =  bol;
    },
    checkminSellPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.minSellPriceClass = !re.test(String(str));
    },
    checkmaxBuyPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.maxBuyPriceClass = !re.test(String(str));
    },
    checkminTurnover(str) {
      let re = /\d|\d+.\d+$/g;
      this.minTurnoverClass = !re.test(String(str));
    },
    checksort(str) {
      let bol = !(str*1>=0&&String(str).split('.').length===1)
                || str===null || !str.trim().length;
      this.sortClass =  bol;
    },
    checkpublishPrice(str) {
      let re = /\d|\d+.\d+$/g;
      this.publishPriceClass = !re.test(String(str));
    },
    confirmExchangeEngineMoel(){
      this.exchangeEngineModel = false;
    },
		confirmLoginPass() {
      this.$Loading.start();
      if(this.operation == 1){
          // 修改币币信息
  			  this.feeClass = false;
          let obj ={
            symbol:  this.fixSymbol,
            fee: this.fee,
  					enable: this.fixEnable,
            visible: this.fixVisible,
            exchangeable: this.fixExchangeable,
            enableMarketSell: this.fixEnableMarketSell,
            enableMarketBuy: this.fixEnableMarketBuy,
            maxBuyPrice: this.fixMaxBuyPrice,
            minTurnover: this.fixMinTurnover,
            sort: this.fixSort,
  					password: this.loginPW
  				}

          fixBBSetting(obj).then( res => {
            if (!res.code) {
  						this.$Message.success('修改成功！');
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else {
              this.$Message.error('修改失败！');
              this.$Loading.error();
            }
          });
      }else if(this.operation == 2){
          // 启动交易引擎
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }

          startBBTrader(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ '】  启动成功，请刷新查看结果！',
                    desc: res.message+" 行情引擎将于2分钟内启动。",
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ '】  启动失败',
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });

      }else if(this.operation == 3){
          // 停止交易引擎
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }

          stopBBTrader(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ '】  停止成功！',
                    desc: res.message+" 行情引擎将于2分钟内停止。",
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ '】  停止失败',
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });
      }else if(this.operation == 4){
          // 撤销所有委托
          let obj ={
            symbol:  this.targetSymbol,
            password: this.loginPW
          }
          cancelBBAllOrders(obj).then( res => {
            if (!res.code) {
              this.$Notice.success({
                    title: "【" + this.targetSymbol+ '】  撤销成功！',
                    desc: res.message,
                    duration: 10
                });
              this.current = 1;
              this.refreshdata(1);
              this.$Loading.finish();
            }else{
              this.$Notice.error({
                    title: "【" + this.targetSymbol+ '】  撤销失败',
                    desc: res.message,
                    duration: 10
                });
              this.$Loading.error();
            }
          });
      }
		},
    alterRobotClicked(){
      if(this.robotMode == 0) {
        // 创建机器人
        createRobotConfig(this.robotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: "创建成功！",
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: "创建失败",
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }else{
        setRobotConfig(this.robotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: "修改成功！",
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: "修改失败",
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }
    },
    alterPriceRobotClicked(){
      if(this.priceRobotMode == 0) {
        // 创建机器人
        createPriceRobotConfig(this.priceRobotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: "创建成功！",
                      desc: res.message,
                      duration: 10
                  });
              this.robotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: "创建失败",
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }else{
        setPriceRobotConfig(this.priceRobotParams).then( res => {
            if(!res.code) {
              this.$Notice.success({
                      title: "修改成功！",
                      desc: res.message,
                      duration: 10
                  });
              this.priceRobotModel = false;
              this.refreshdata(1);
            }else{
              this.$Notice.error({
                      title: "修改失败",
                      desc: res.message,
                      duration: 10
                  });
              }
          });
      }
    },
    confirmClicked() {
			this.loginPassModal =  true;
    },
    confirmAudit() {
      let judgeCondition = this.baseCoinScaleClass || this.coinScaleClass || this.feeClass ||
                  this.baseSymbolClass ||this.coinSymbolClass ||this.checkSymbolClass || this.minVolumeClass || this.maxVolumeClass ||
                  this.zoneClass || this.minSellPriceClass || this.maxBuyPriceClass;
      let judgeEmpty = !this.symbol || !this.coinSymbol || !this.baseSymbol
                      || !this.fee || !this.coinScale || !this.baseCoinScale;

      if(judgeCondition || judgeEmpty) {
        this.$Message.warning('信息录入不正确！');
      } else {
        let obj= {
          symbol: this.symbol,
          coinSymbol: this.coinSymbol,
          baseSymbol: this.baseSymbol,
          enable: this.enable,
          fee: this.fee,
          coinScale: this.coinScale,
          baseCoinScale: this.baseCoinScale,
          minVolume: this.minVolume,
          maxVolume: this.maxVolume,
          zone: this.zone,
          minSellPrice: this.minSellPrice,
          maxBuyPrice: this.maxBuyPrice,
          minTurnover: this.minTurnover,
          sort: this.sort,
          publishPrice: this.publishPrice,
          publishType: this.publishType,
          publishAmount: this.publishAmount,
          startTime: dtime(this.startTime).format('YYYY-MM-DD HH:mm:ss'),
          endTime: dtime(this.endTime).format('YYYY-MM-DD HH:mm:ss'),
          clearTime: dtime(this.clearTime).format('YYYY-MM-DD HH:mm:ss'),
          visible: this.visible,
          robotType: this.robotType,
          exchangeable: this.exchangeable,
          enableMarketBuy: this.enableMarketBuy,
          enableMarketSell: this.enableMarketSell,
        }
        addBBSetting(obj).then( res => {
          if(!res.code) {
            this.$Notice.success({
                    title: "添加成功！",
                    desc: res.message,
                    duration: 10
                });
            this.current = 1;
            this.refreshdata(1);
          }else{
            this.$Notice.error({
                    title: "添加失败",
                    desc: res.message,
                    duration: 10
                });
          }
        })
      }
    },
    selected(selection) {
      this.deleteArr = [];
      selection.forEach(item => {
        this.deleteArr.push(item.symbol)
      });
    },
    confirmDel() {
      delBBSetting({ ids: this.deleteArr }).then( res =>{
        if(!res.code) {
          this.$Notice.success({
                    title: "删除成功！",
                    desc: res.message,
                    duration: 10
                });
          this.current = 1;
          this.refreshdata(1);
        }else {
          this.$Notice.error({
                    title: "删除失败！",
                    desc: res.message,
                    duration: 10
                });
        }
      })
    },
    refreshPageManual() {
      this.ifLoading = true;
      this.refreshdata(this.current);
    },
    changePage(pageIndex) {
      this.ifLoading = true;
      this.current = pageIndex;
      this.refreshdata(pageIndex);
    },
    search(){
      this.refreshdata(1);
    },
    refreshdata(pageIndex) {

      queryBBSetting({ pageNo: pageIndex, pageSize: 50, coinSymbol: this.searchSymbol, baseSymbol: this.searchBase }).then( res => {
        this.exchange = res.data.content;
        this.pageNum = res.data.totalElements;
        this.ifLoading = false;
      });
    },
    delcoin() {
      if(!this.deleteArr.length) {
        this.$Message.warning('尚未选取项目！');
      } else  this.ifDelete = true;
    },
    addcoin() {
      this.auditModal = true;

      this.symbol = null;
      this.coinSymbol = null;
      this.baseSymbol = null;
      this.enable = "1";
      this.fee = null;
      this.coinScale = null;
      this.baseCoinScal = null;
      this.minVolume = null;
      this.maxVolume = null;
      this.zone = null;
      this.minSellPrice = null;
      this.maxBuyPrice = null;
      this.minTurnover = null;
      this.sort = null;
      this.publishPrice = null; // add  分摊抢购初始价格
      this.publishType = "1"; // 发行活动：1-无活动  2-抢购   3-分摊
      this.publishAmount = 0;
      this.startTime = null;
      this.endTime = null;
      this.clearTime = null;
      this.visible = "1";
      this.robotType = "0";
      this.exchangeable = "1";
      this.enableMarketBuy = "1";
      this.enableMarketSell = "1";
    },
  },
  created() {
    this.refreshdata(1);
  }
};
</script>

<style lang="less" scoped>
  .auditModel{
    ul {
      padding-left: 20px;
      li {
        position: relative;
        margin-bottom: 18px;
        span{
          position: absolute;
          top: 0;
          left: 0;
          display: inline-block;
          width: 95px;
          text-align: right;
          i{
            font-size: 14px;
            font-weight: 700;
            color: #ec0909;
          }
        }
        p{
          padding-left: 100px;
          min-width: 300px;
          line-height: 32px;
          em{
            position: static;
            color: unset;
          }
        }
      }
    }
  }
  .setting-title{
    font-size:14px;font-weight:bold;padding-bottom:20px;
  }
.auditModel ul li>em{
    position: absolute;
    bottom: -15px;
    font-size:10px;
    margin-left: 100px;
    line-height:12px;
    font-style: normal;
    color: #ec0909;
  }
</style>
