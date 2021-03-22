document.getElementById("service-info-list").setAttribute("class", "govuk-list govuk-!-padding-bottom-5")

document.getElementById("service-info-home-link").setAttribute("class", "govuk-link")
document.getElementById("service-info-messages-link").setAttribute("class", "govuk-link")
document.getElementById("service-info-manage-account-link").setAttribute("class", "govuk-link")
document.getElementById("service-info-help-and-contact-link").setAttribute("class", "govuk-link")

let serviceInfoList = document.getElementById("service-info-list")
let homeLink = document.getElementById("service-info-home-link")
let messagesLink = document.getElementById("service-info-messages-link")
let manageAccountLink = document.getElementById("service-info-manage-account-link")
let helpAndContactLink = document.getElementById("service-info-help-and-contact-link")

if(serviceInfoList != null) serviceInfoList.setAttribute("class", "govuk-list govuk-!-padding-bottom-5")
if(homeLink != null) homeLink.setAttribute("class", "govuk-link")
if(messagesLink != null) messagesLink.setAttribute("class", "govuk-link")
if(manageAccountLink != null) manageAccountLink.setAttribute("class", "govuk-link")
if(helpAndContactLink != null) helpAndContactLink.setAttribute("class", "govuk-link")