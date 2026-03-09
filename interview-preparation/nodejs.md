# Nodejs fundamental

## Event loop

Các function được call trong js sẽ dc đưa vào call stack, kể cả async function. Async function nhanh chóng được push out khỏi call stack và 1 thg khác sẽ chịu trách nhiệm thực thi nó, js web thì là web api, node thì là libuv, sau đó đẩy success callback về task queue. Event loop có trách nhiệm liên tục check nếu call stack empty thì sẽ lấy task trong task queue đẩy vào call stack để main thread execute callback function.

Ngoài ra còn có 1 queue khác là microtask queue có độ ưu tiên cao hơn task queue, dành cho Promise, async/await, ...