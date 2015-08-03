function selectChannel()
{
    document.getElementById(channelId).setAttribute("selected", "true");
}
selectChannel();

var delBtn = document.getElementById("delBtn"),
    sel = document.getElementById("channelNames");
function changeHandler()
{
    var val = sel.value;
    if (val === "" || val === "all") {
        delBtn.setAttribute("disabled", true);
    } else {
        delBtn.removeAttribute("disabled");
    }
};
sel.onchange = changeHandler;
changeHandler();

function sendRequest(address, params)
{
    var http = new  XMLHttpRequest();
    http.open("POST", address, false);
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.send(params);
    document.open();
    document.write(http.responseText);
    document.close();
}

function setFilter()
{
    var filterContainer = document.getElementById("filters");
    keyword = filterContainer.getElementsByClassName("filter")[0].value;
    beginDate = filterContainer.getElementsByClassName("filter")[1].value;
    endDate = filterContainer.getElementsByClassName("filter")[2].value;
}

var dateSelectors = document.getElementsByName("dateOrder");
function dateHandler()
{
    setFilter();
    var orders = document.getElementsByName("dateSort"),
    orderValue = document.querySelector('input[name="dateOrder"]:checked').value;
    for (var i = 0; i < orders.length; ++i)
        orders[i].value = this.value;
    var params = "userId=" + userId + "&channelId=" + channelId + "&dateSort=" + orderValue +
        "&keyword=" + keyword + "&beginDate=" + beginDate + "&endDate=" + endDate;
    sendRequest("./parseRss", params);
    
};
dateSelectors[0].onchange = dateHandler;
dateSelectors[1].onchange = dateHandler;

function switchChannel (id)
{
    setFilter();
    var params = "userId=" + userId + "&channelId=" + id + "&dateSort=" + orderValue +
        "&keyword=" + keyword + "&beginDate=" + beginDate + "&endDate=" + endDate;
    sendRequest("./parseRss", params);
};
var list = document.getElementById("channelNames");
list.onchange = function() {switchChannel(this.options[this.selectedIndex].getAttribute("id"));};

function addParameters(item)
{
    var params = document.getElementById("common").cloneNode(true);
    params.removeAttribute("id");
    item.parentElement.appendChild(params);
    var filters = document.getElementById("filters").getElementsByClassName("filter");
    for (var i = 0; i < filters.length; ++i)
    {
        el = document.createElement("input");
        el.type = "hidden";
        el.name = filters[i].getAttribute("name");
        el.value = filters[i].value;
        item.parentElement.appendChild(el);
    }
}

function clearFilter()
{
    var filters = document.getElementById("filters").getElementsByClassName("filter");
    for (var i = 0; i < filters.length; ++i)
        filters[i].value = "";
}

function delNews(id)
{
    setFilter();
    var params = "userId=" + userId + "&newsId=" + id + "&dateSort=" + orderValue +
        "&keyword=" + keyword + "&beginDate=" + beginDate + "&endDate=" + endDate;
    sendRequest("./deleteNews", params);
}

function markNews(id, read)
{
    setFilter();
    var params = "userId=" + userId + "&newsId=" + id + "&channelId=" +
        channelId + "&dateSort=" + orderValue + "&isRead=" + read +
        "&keyword=" + keyword + "&beginDate=" + beginDate + "&endDate=" + endDate;
    sendRequest("./updateReadAttr", params);
}

function changePage(sender)
{
    setFilter();
    var params = "userId=" + userId + "&channelId=" +
        channelId + "&dateSort=" + orderValue +
        "&keyword=" + keyword + "&beginDate=" + beginDate + "&endDate=" + endDate + 
        "&pageNum=" + sender.value;
    sendRequest("./parseRss", params);
}

function uploadFile(sender)
{
    addParameters(sender);
    sender.parentElement.submit();
}
