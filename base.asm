print_int:
  push rbx
  push rcx
  push rdx
  push rsi
  push rdi
  push r8
  mov ecx, eax
  xor r8b, r8b
  cmp ecx, 0
  jge .unsigned
  neg ecx
  mov r8b, 1

.unsigned:
  lea r9, [buf + 31]
  xor r10, r10
  mov eax, ecx
  mov ebx, 10
  cmp eax, 0
  jne .loop
  mov byte [r9], '0'
  dec r9
  inc r10
  jmp .done

.loop:
  xor edx, edx
  div ebx
  add dl, '0'
  mov [r9], dl
  dec r9
  inc r10
  mov eax, eax
  cmp eax, 0
  jne .loop

.done:
  cmp r8b, 0
  je .no_sign
  mov byte [r9], '-'
  dec r9
  inc r10

.no_sign:
  lea rsi, [r9 + 1]
  mov rdx, r10
  mov rax, 1
  mov rdi, 1
  syscall
  mov rax, 1
  mov rdi, 1
  lea rsi, [rel nl]
  mov rdx, 1
  syscall
  pop r8
  pop rdi
  pop rsi
  pop rdx
  pop rcx
  pop rbx
  ret