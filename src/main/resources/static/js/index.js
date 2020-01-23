$(function () {
    var i = 0;
    setInterval(function () {
        if (($("#"+i.toString()).val()!="")&&($("#"+i.toString()+"1").val()!="")){
            i++;
            $("#wfin").append("<div class=\"input-group\" id=\"div"+i.toString()+"\""+">\n" +
                "                    <input type=\"text\" class=\"form-control\" placeholder=\"E\" id=\""+i.toString()+"\" name="+i.toString()+""+">\n" +
                "                    <span class=\"input-group-addon\">- ></span>\n" +
                "                    <input type=\"text\" class=\"form-control\" placeholder=\"TE\"id=\""+i.toString()+"1\" name="+i.toString()+"1"+">\n" +
                "                </div>")
        }
        var i1 = i-1;
        if (($("#"+(i1).toString()).val()=="")&&($("#"+(i1).toString()+"1").val()=="")){
            $("#div"+i.toString()).remove();
            i--;
            console.log("true");
        }
        else {
            console.log("false");
        }
    },100);
    $("#btn").click(function () {
        console.log($('form').serializeArray());
       $.post("./lr1Analyze",{data:JSON.stringify($("#wfin").serializeArray()),inString:$("#in").val()},function (result) {

           if (result.statement=="Accept!"){
               $("#result h1").removeAttr("style");
               $("#result").append("<table class=\"table table-striped\">\n" +
                   "\t<thead>\n" +
                   "\t\t<tr>\n" +
                   "\t\t\t<th>步 骤</th>\n" +
                   "\t\t\t<th>状态栈</th>\n" +
                   "\t\t\t<th>符号栈</th>\n" +
                   "\t\t\t<th>输入串</th>\n" +
                   "\t\t\t<th>动作说明</th>\n" +
                   "\t\t</tr>\n" +
                   "\t</thead>\n" +
                   "\t<tbody>\n" +
                   "\t</tbody>\n" +
                   "</table>");
                var jsonObjs = result.result;
                for (var i=0;i<jsonObjs.length;i++){
                    $("tbody").append("<tr>\n" +
                        "\t\t\t<td>"+jsonObjs[i].order+"</td>\n" +
                        "\t\t\t<td>"+jsonObjs[i].stateStack+"</td>\n" +
                        "\t\t\t<td>"+jsonObjs[i].charStack+"</td>\n" +
                        "\t\t\t<td>"+jsonObjs[i].inString+"</td>\n" +
                        "\t\t\t<td>"+jsonObjs[i].action+"</td>\n" +
                        "\t\t</tr>")
                }
// #5cb85c
                $("#btn").attr("class","btn-lg btn-success");
               $("#btn").attr("value","Accept!");
                $("#in").val(result.statement);
                $("#in").attr("style","background: #5cb85c");
               $("#src").text("最终结果");
           }
           else {
               $("#btn").attr("class","btn-lg btn-danger");
               $("#btn").attr("value","ERROR!");
               $("#in").val(result.statement);
               $("#in").attr("style","background: #d9534f");
               $("#src").text("最终结果");
           }
            $("#result2 h1").removeAttr("style");
           $("#result2").append("<table class=\"table table-bordered\">\n" +
               "\t<thead>\n" +
               "\t\t<tr id = \"head\">\n" +
               "\t\t\t<th>状态</th>\n" +
               "\t\t</tr>\n" +
               "\t</thead>\n" +
               "\t<tbody id='body'>\n" +
               "\t\t\n" +
               "\t</tbody>\n" +
               "</table>");
           for (var j = 0;j<result.ActionSet.length;j++){
               $("#head").append("<th>"+result.ActionSet[j]+"</th>")
           }
           for (var k = 0;k<result.GotoSet.length;k++){
               $("#head").append("<th>"+result.GotoSet[k]+"</th>")
           }
           for (var m = 0;m<21;m++){

               var string = "<td>"+m+"</td>\n";
               // $("#body").append("<td>"+m+"</td>\n");
               for (var key = 0;key<result.ActionSet.length;key++){
                   var jsono = result.ActionMap[m];
                   if (jsono[result.ActionSet[key]]==undefined){
                       string=string+ "\t\t\t<td>"+""+"</td>\n" +
                           "\t\t\t\n";
                   }else {
                       string=string+"\t\t\t<td>"+jsono[result.ActionSet[key]]+"</td>\n" +
                           "\t\t\t\n";
                   }
               }
               for (var key2=0; key2<result.GotoSet.length;key2++){
                   var jsono1 = result.GotoMap[m];
                   if (jsono1[result.GotoSet[key2]]==undefined){

                       string=string+"\t\t\t<td>"+""+"</td>\n" +
                           "\t\t\t\n";
                   }else {

                       string=string+"\t\t\t<td>"+jsono1[result.GotoSet[key2]]+"</td>\n" +
                           "\t\t\t\n";
                   }

               }
               $("#body").append("<tr>"+string+"</tr>");

           }
       })
    });
})