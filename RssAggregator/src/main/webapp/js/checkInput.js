var inputs = document.getElementsByClassName("data");
var inputsCount = inputs.length;

function checkData()
{
    var btn = document.getElementsByClassName("btn")[0];
    for (var i = 0; i < inputsCount; ++i)
    {
        if (inputs[i].value == "")
        {
            btn.setAttribute("disabled", true);
            return;
        }
    }
    btn.removeAttribute("disabled");
}

for (var i = 0; i < inputsCount; ++i)
    inputs[i].oninput = checkData;
