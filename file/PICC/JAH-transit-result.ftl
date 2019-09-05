{
  "code": "${ROOT.ReturnInfo.GeneralInfoReturn.ErrorCode}",
  "uuid": "${ROOT.ReturnInfo.GeneralInfoReturn.UUID}",
  "msg": "${ROOT.ReturnInfo.GeneralInfoReturn.ErrorMessage}",
  "value": {
    "proposalNo": "${ROOT.ReturnInfo.PolicyInfoReturns.PolicyInfoReturn.ProposalNo}",
    "payUrl": "<@regular pattern="投保单(.*)自动核保通过，需见费转保单!交费通知单号为：(.*),微信交费链接为：(.*)" group="3">${ROOT.ReturnInfo.PolicyInfoReturns.PolicyInfoReturn.SaveMessage}</@regular>"
  }
}