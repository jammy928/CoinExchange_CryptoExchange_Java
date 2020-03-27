const $ = require('jquery');
exports = module.exports = {
    clickScTab:function(){
        $('.sc_filter').children().click(function(){
            $(this).addClass('active').siblings().removeClass('active');
            if($(this)[0].tagName=='SPAN'){
                var a=$(this).html();
                $('#'+a).show().siblings('.ivu-table-wrapper').hide();
            }else{
                $('#collect').show().siblings('.ivu-table-wrapper').hide();
            }
        });
        
        $('.sidebar tbody .ivu-icon').click(function(){
                if($(this).attr('class')=='ivu-icon ivu-icon-android-star-outline'){
                    $(this).attr('class','ivu-icon ivu-icon-android-star');
                }else{
                    $(this).attr('class','ivu-icon ivu-icon-android-star-outline');
                }
        });

        $('.mod_tab li').click(function(){
            $(this).addClass('active').siblings().removeClass('active');
        })
        $('.history li').click(function(){
            $(this).addClass('active').siblings().removeClass('active');
        })
    },
    getKline:function(){
            
    }
}