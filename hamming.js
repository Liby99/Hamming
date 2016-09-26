var Hamming = {
    parityCoversDigit: [[0, 1, 3],  //p1覆盖d1, d2, d4
                        [0, 2, 3],  //p2覆盖d1, d3, d4
                        [1, 2, 3]], //p3覆盖d2, d3, d4
                        
    /**
     * Data should be a length 4 string: [d1, d2, d3, d4]
     * Return a length 8 encoded data
     */
    encode: function (data) {
        
        //计算每一个Parity Bit
        var parity = [0, 0, 0]; //[p1, p2, p3]先初始化为0
        for (var i = 0; i < parity.length; i++) {
            var sum = 0; //用于记录当前Parity Bit覆盖了多少个1
            for (var j = 0; j < this.parityCoversDigit[i].length; j++) {
                sum += parseInt(data[this.parityCoversDigit[i][j]]);
            }
            parity[i] = sum % 2; //如果覆盖了偶数个1，那么这个Parity Bit为0；如果奇数个，则为1
        }
        
        //将parity bits和data bits合起来，得到一个七位的新的data
        var ret = "" + parity[0] + parity[1] + data[0] + parity[2] + data[1] + data[2] + data[3];
        
        //开始给这个新的七位data附加一个最后的Parity Bit
        var sum = 0; //开始找这个七位data里面有多少个1
        for (var i = 0; i < ret.length; i++) {
            sum += parseInt(ret[i]);
        }
        ret += sum % 2; //如果共有偶数个，则这个最后附加的Parity Bit为0；如果奇数个，则为1
        
        //返回这个加密后的数据
        return ret;
    },
    
    /**
     * Encoded should be a length 8 encoded data.
     */
    decode: function (encoded) {
        
        //把必要的数据都好好地存下来
        var parity = [encoded[0], encoded[1], encoded[3]];
        var finalParity = encoded[7];
        var data = [encoded[2], encoded[4], encoded[5], encoded[6]];
        
        //设置一个正确的Parity
        var correctParity = [0, 0, 0]; //先初始化
        for (var i = 0; i < correctParity.length; i++) {
            var sum = 0;
            for (var j = 0; j < this.parityCoversDigit[i].length; j++) {
                sum += parseInt(data[this.parityCoversDigit[i][j]]);
            }
            correctParity[i] = sum % 2;
        }
        
        //比较正确的Parity和encoded中的parity是否相同
        var comparison = [false, false, false]; //初始化。相同即是true，不同即是false
        for (var i = 0; i < parity.length; i++) {
            comparison[i] = (parity[i] == correctParity[i]);
        }
        
        //开始根据比较的内容决定到底要修改data中的哪一位
        var incorrectDataBit = -1; //初始化为-1
        if (!comparison[0] && !comparison[1] && comparison[2]) { //p1和p2错误，p3正确
            incorrectDataBit = 1;
        }
        if (!comparison[0] && comparison[1] && !comparison[2]) { //p1和p3错误，p2正确
            incorrectDataBit = 0;
        }
        if (comparison[0] && !comparison[1] && !comparison[2]) { //p2和p3错误，p1正确
            incorrectDataBit = 2;
        }
        if (!comparison[0] && !comparison[1] && !comparison[2]) { //如果三个都错了
            incorrectDataBit = 3;
        }
        
        //如果有错，将那一位翻转，并检查此修复是否有错
        if (incorrectDataBit != -1) {
            data[incorrectDataBit] = (data[incorrectDataBit] == 0) ? 1 : 0;
            
            //统计新的数据里面共有多少个1
            var newEncoded = [parity[0], parity[1], data[0], parity[2], data[1], data[2], data[3], finalParity];
            var sum = 0;
            for (var i = 0; i < newEncoded.length; i++) {
                sum += parseInt(newEncoded[i]);
            }
            
            //如果是偶数个1，则正确，直接返回data
            //如果是奇数个1，则修改的不正确，弹出错误
            if (sum % 2 == 0) {
                return "" + data[0] + data[1] + data[2] + data[3];
            }
            else {
                console.log("Contains 2 Errors. Unable to recover");
                return "";
            }
        }
        else {
            
            //如果incorrectDataBit是-1，即表明没错，或，至少在data中没有错误。此时直接返回data就好
            return "" + data[0] + data[1] + data[2] + data[3];
        }
    }
}

console.log("\nTESTING 1011 --- Encoded: 01100110");
console.log(Hamming.encode("1011"));
console.log(Hamming.decode("01100110")); //Correct One
console.log(Hamming.decode("11100110")); //Has One Error
console.log(Hamming.decode("10100110")); //Has Two Errors

console.log("\nTESTING 0101 --- Encoded: 01001011");
console.log(Hamming.encode("0101"));
console.log(Hamming.decode("01001011"));
console.log(Hamming.decode("11001011"));
console.log(Hamming.decode("10001011"));
