{
  "uuid": "${ROOT.uuid}",
  "p-sex": "${ROOT.people.sex}",
  "p-name": "${ROOT.people.name}",
  "list": [
<#list ROOT.people.people as obj>
    {
      "c-name": "${obj.name}",
      "c-age": "${obj.age}",
      "c-sex": "${obj.sex}"
    }<#if obj_has_next>,</#if>
</#list>
  ]
}