var request = new XMLHttpRequest;
request.open("POST", "https://companion-api.battlefield.com/jsonrpc/web/api?Emblems.newPrivateEmblem", !0), request.onreadystatechange = function () {
    if (request.readyState == XMLHttpRequest.DONE) {
        var e = JSON.parse(request.responseText);
        e.result ? window.location.href = window.location.href.replace("/new", "/edit/") + e.result.slot : alert("Error")
    }
}, request.setRequestHeader("Content-Type", "application/json;charset=UTF-8"), request.setRequestHeader("X-GatewaySession", localStorage.gatewaySessionId), data = {
    jsonrpc: "2.0",
    method: "Emblems.newPrivateEmblem",
    params: {data: '[{"opacity":1,"angle":-10.988651096493973,"flipX":false,"flipY":false,"top":190.55075,"left":155.05075,"height":196.1042542983462,"width":196.1042542983462,"asset":"Shield","fill":"#ffd966","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":46.75775,"left":154.25775,"height":63.10240572171652,"width":204.75000000000003,"asset":"Circle","fill":"#ffd966","selectable":false},{"opacity":1,"angle":107.47863560595438,"flipX":false,"flipY":false,"top":42.331999999999994,"left":133.5,"height":119.05198626036368,"width":18,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":93.74752040924582,"flipX":true,"flipY":false,"top":96.332,"left":154,"height":64.20483135996888,"width":7.00805800680438,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":93.74752040924582,"flipX":true,"flipY":true,"top":108.1797,"left":153.0039,"height":64.20483135996888,"width":7.00805800680438,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-96.71125498390424,"flipX":true,"flipY":true,"top":103.238275,"left":109.71875,"height":24.188137656021922,"width":7.596278939572662,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-88.90714077507681,"flipX":true,"flipY":true,"top":108.09375,"left":193.125,"height":24.188137656021922,"width":7.596278939572662,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-88.90714077507681,"flipX":true,"flipY":true,"top":96.59375,"left":193.125,"height":24.188137656021922,"width":7.596278939572662,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-88.90714077507681,"flipX":true,"flipY":true,"top":90.59375,"left":111.625,"height":24.188137656021922,"width":7.596278939572662,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":152.68993636014977,"flipX":false,"flipY":false,"top":187.0586,"left":157.78125,"height":33.12294066526509,"width":23.564488062786218,"asset":"Number7","fill":"#f1c232","selectable":false},{"opacity":1,"angle":2.6897986767932216,"flipX":false,"flipY":false,"top":137.12900000000002,"left":151.457,"height":236.26445297142396,"width":219.63029982062102,"asset":"Shield5","fill":"#ffd966","selectable":false},{"opacity":1,"angle":-52.54942176826327,"flipX":false,"flipY":false,"top":202,"left":135,"height":28.058563533293484,"width":2.9959862499615406,"asset":"Stroke","fill":"#f1c232","selectable":false},{"opacity":1,"angle":27.049212432680225,"flipX":true,"flipY":true,"top":215.488275,"left":108.996095,"height":39.77569764094192,"width":1.8744323003507841,"asset":"Stroke","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-26.95320277868415,"flipX":true,"flipY":true,"top":218.9101575,"left":196.48047,"height":39.77569764094192,"width":2.1742411830449537,"asset":"Stroke","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-76.23442124394478,"flipX":false,"flipY":false,"top":244.21474999999998,"left":155.113275,"height":44.267160938837556,"width":8.0946107215486,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":99.00079605022192,"flipX":true,"flipY":true,"top":240.488275,"left":158.546875,"height":59.09565462121639,"width":9.213240661583606,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":9.199573364721589,"flipX":false,"flipY":false,"top":137.78515,"left":106,"height":20.571753124999997,"width":47,"asset":"Eye2","fill":"#ffffff","selectable":false},{"opacity":1,"angle":0.042023052931548524,"flipX":true,"flipY":true,"top":141.1211,"left":197.980475,"height":19.243066237675038,"width":43.964367435054314,"asset":"Eye2","fill":"#ffffff","selectable":false},{"opacity":1,"angle":-48.2262221506495,"flipX":false,"flipY":false,"top":153.832,"left":183.5,"height":27.663999999999987,"width":4.601757267916254,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":50.04053732271392,"flipX":true,"flipY":true,"top":152.667975,"left":120.3007825,"height":27.663999999999987,"width":4.601757267916254,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":29.447423960318694,"flipX":true,"flipY":true,"top":71.86725,"left":62,"height":101.73487305150817,"width":65,"asset":"Wing4","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-71.93952806380082,"flipX":true,"flipY":true,"top":30,"left":182,"height":105.12583774741594,"width":18,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-71.93952806380082,"flipX":true,"flipY":true,"top":41.5,"left":152.5,"height":105.12583774741594,"width":18,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-71.93952806380082,"flipX":true,"flipY":true,"top":39.5,"left":161,"height":105.12583774741594,"width":18,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-71.93952806380082,"flipX":true,"flipY":true,"top":37.5,"left":172.5,"height":105.12583774741594,"width":18,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":91.20581466414,"flipX":false,"flipY":false,"top":24.74225,"left":162.5195,"height":168.2821004681199,"width":28.114488748155313,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":82.43470538958064,"flipX":false,"flipY":false,"top":15.773499999999999,"left":137.6797,"height":112.58491989424384,"width":26.293534250334595,"asset":"StrokeBent","fill":"#ffffff","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":137,"left":97.5,"height":17,"width":17,"asset":"Circle","fill":"#000000","selectable":false},{"opacity":1,"angle":0,"flipX":true,"flipY":true,"top":141,"left":190.5,"height":17,"width":17,"asset":"Circle","fill":"#000000","selectable":false},{"opacity":1,"angle":120.26356151047422,"flipX":false,"flipY":false,"top":125.332,"left":116,"height":52.66399999999999,"width":14.825689280700702,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-109.33469870748792,"flipX":true,"flipY":true,"top":126.207025,"left":195.91015,"height":52.66399999999999,"width":14.825689280700702,"asset":"StrokeBent","fill":"#d9d9d9","selectable":false},{"opacity":1,"angle":-107.57346736326102,"flipX":false,"flipY":false,"top":137.48825,"left":77.796875,"height":14.336000000000013,"width":4.70333681889764,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":126.32676560452732,"flipX":true,"flipY":true,"top":148.167975,"left":224.6484375,"height":14.336000000000013,"width":4.70333681889764,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":113.73279583180438,"flipX":false,"flipY":false,"top":128.332,"left":113.5,"height":41.66399999999999,"width":10,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":-101.81872373166306,"flipX":true,"flipY":true,"top":130.664075,"left":195.5,"height":41.66399999999999,"width":10,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":168.59787899063488,"flipX":false,"flipY":false,"top":213.332,"left":74,"height":44.66399999999999,"width":5.23028148259912,"asset":"StrokeBent","fill":"#f1c232","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":37.64075,"left":291,"height":70.2814773166532,"width":50,"asset":"Number2","fill":"#ffffff","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":115.37100000000001,"left":289.5,"height":67.74733068460088,"width":29.999999999999996,"asset":"Number1","fill":"#ffffff","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":193.64075,"left":289,"height":70.2814773166532,"width":50,"asset":"Number3","fill":"#ffffff","selectable":false},{"opacity":1,"angle":0,"flipX":false,"flipY":false,"top":274.14075,"left":288,"height":70.2814773166532,"width":50,"asset":"Number7","fill":"#ffffff","selectable":false}]'},
    id: "00000000-0000-0000-0000-000000000000"
}, request.send(JSON.stringify(data));
