const http = require("http");
const crypto = require("crypto");
const fs = require("fs").promises;

const host = "localhost";
const port = 8080;
let indexFile;
let sockets = [];

const server = http.createServer((req, res) => {
	res.setHeader("Content-Type", "text/html");
	res.writeHead(200);
	res.end(indexFile);
});

fs.readFile(`${__dirname}/index.html`)
	.then((contents) => {
		indexFile = contents;
		server.listen(port, host, () =>
			console.log(`Server is running on http://${host}:${port}`)
		);
	})
	.catch((err) => {
		console.error(`Could not read index.html file: ${err}`);
		process.exit(1);
	});

server.on("upgrade", (req, socket) => {
	if (req.headers["upgrade"] !== "websocket") {
		socket.end("HTTP/1.1 400 Bad Request");
		return;
	}
	const key = req.headers["sec-websocket-key"];
	const hash = generateAcceptResponse(key);
	const responseHeaders = [
		"HTTP/1.1 101 Web Socket Protocol Handshake",
		"Upgrade: WebSocket",
		"Connection: Upgrade",
		`Sec-WebSocket-Accept: ${hash}`,
	];

	const protocol = req.headers["sec-websocket-protocol"];
	const protocols = protocol ? protocol.split(",").map((s) => s.trim()) : [];
	if (protocols.includes("json")) {
		responseHeaders.push(`Sec-WebSocket-Protocol: json`);
	}
	socket.write(responseHeaders.join("\r\n") + "\r\n\r\n");

	sockets.push(socket);
	try {
		sockets.forEach((s) =>
			s.write(
				constructReply({
					connected: sockets.length,
				})
			)
		);
	} catch (e) {
		console.log("Error:", e);
	}

	socket.on("data", (buffer) => {
		try {
			const message = parseMessage(buffer);
			if (message) {
				sockets.forEach((s) =>
					s.write(
						constructReply({
							message: message.message,
						})
					)
				);
			} else if (message === null) {
				sockets = sockets.filter((s) => s !== socket);
				sockets.forEach((s) =>
					s.write(
						constructReply({
							connected: sockets.length,
						})
					)
				);
			}
		} catch (e) {
			console.log("Error:", e);
		}
	});
});

const constructReply = (data) => {
	const json = JSON.stringify(data);
	const jsonByteLength = Buffer.byteLength(json);
	const lengthByteCount = jsonByteLength < 126 ? 0 : 2;
	const payloadLength = lengthByteCount === 0 ? jsonByteLength : 126;
	const buffer = Buffer.alloc(2 + lengthByteCount + jsonByteLength);

	buffer.writeUInt8(0b10000001, 0);
	buffer.writeUInt8(payloadLength, 1);

	let payloadOffset = 2;
	if (lengthByteCount > 0) {
		buffer.writeUInt16BE(jsonByteLength, 2);
		payloadOffset += lengthByteCount;
	}

	buffer.write(json, payloadOffset);
	return buffer;
};

const parseMessage = (buffer) => {
	const firstByte = buffer.readUInt8(0);
	const opCode = firstByte & 0xf;
	if (opCode === 0x8) {
		return null;
	}
	if (opCode !== 0x1) {
		return;
	}
	const secondByte = buffer.readUInt8(1);
	const isMasked = Boolean((secondByte >>> 7) & 0x1);

	let currentOffset = 2;
	let payloadLength = secondByte & 0x7f;
	if (payloadLength > 125) {
		if (payloadLength === 126) {
			payloadLength = buffer.readUInt16BE(currentOffset);
			currentOffset += 2;
		} else {
			throw new Error("Large payloads is not supported");
		}
	}

	let maskingKey;
	if (isMasked) {
		maskingKey = buffer.readUInt32BE(currentOffset);
		currentOffset += 4;
	}
	const data = Buffer.alloc(payloadLength);
	if (isMasked) {
		for (let i = 0, j = 0; i < payloadLength; ++i, j = i % 4) {
			const shift = j == 3 ? 0 : (3 - j) << 3;
			const mask = (shift == 0 ? maskingKey : maskingKey >>> shift) & 0xff;
			const source = buffer.readUInt8(currentOffset++);
			data.writeUInt8(mask ^ source, i);
		}
	} else {
		buffer.copy(data, 0, currentOffset++);
	}
	const json = data.toString("utf8");
	return JSON.parse(json);
};

const generateAcceptResponse = (key) => {
	return crypto
		.createHash("sha1")
		.update(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11", "binary")
		.digest("base64");
};
