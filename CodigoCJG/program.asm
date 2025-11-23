	section	.data
nl	DB	10
msg_0	DB	'FIN', 0xA, 0xD
msgLen_0	EQU	$- msg_0
y_main	DW	0
msg_2	DB	'Par', 0xA, 0xD
msgLen_2	EQU	$- msg_2
msg_4	DB	'Impar', 0xA, 0xD
msgLen_4	EQU	$- msg_4
	section	.text
	global	_start
_start:		
	MOV	  WORD [y_main], 10
	MOV	ax, [y_main]
	PUSH	ax
	MOV	ax, 2
	POP	bx
	xchg	ax, bx
	XOR	dx, dx
	DIV	bx
	PUSH	ax
	MOV	ax, 2
	POP	bx
	xchg	ax, bx
	MUL	bx
	PUSH	ax
	MOV	ax, [y_main]
	POP	bx
	xchg	ax, bx
	CMP	ax, bx
	SETE	al
	MOVZX	ax, al
	CMP	ax, 0
	JE	L1
	MOV	edx, msgLen_2
	MOV	ecx, msg_2
	MOV	ebx, 1
	MOV	eax, 4
	INT	0x80
	JMP	L2
L1:		
	MOV	edx, msgLen_4
	MOV	ecx, msg_4
	MOV	ebx, 1
	MOV	eax, 4
	INT	0x80
L2:		
	MOV	edx, msgLen_0
	MOV	ecx, msg_0
	MOV	ebx, 1
	MOV	eax, 4
	INT	0x80
	MOV	ebx, 0
	MOV	eax, 1
	INT	0x80
