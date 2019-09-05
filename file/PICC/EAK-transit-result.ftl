{
    "code": "${ROOT.GeneralInfoReturn.ErrorCode}",
    "uuid": "${ROOT.GeneralInfoReturn.UUID}",
    "msg": "${ROOT.GeneralInfoReturn.ErrorMessage}",
    "value": {
        "orderNo": "${ROOT.PolicyInfoReturns.PolicyInfoReturn.ProposalNo}",
        "payUrl": "<@regular pattern="投保单(.*)自动核保通过，需见费转保单!交费通知单号为：(.*),微信交费链接为：(.*)" group="3">${ROOT.PolicyInfoReturns.PolicyInfoReturn.SaveMessage}</@regular>"
    }
}