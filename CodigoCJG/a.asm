          section     .data
msgApr    DB          'Aprovado', 0xA
msgLenApr EQU         $- msgApr
msgRep    DB          'Reprobado', 0xA
msgLenRep EQU         $- msgRep
a         DD          0

          section     .text
          global      _start

_start:
          MOV         DWORD [a], 51
          MOV         eax, [a]
          MOV         ebx, 50
          CMP         eax, ebx
          JLE         else
          MOV         edx, msgLenApr
          MOV         ecx, msgApr
          MOV         ebx, 1
          MOV         eax, 4
          INT         0x80
          JMP         endif
else:
          MOV         edx, msgLenRep
          MOV         ecx, msgRep
          MOV         ebx, 1
          MOV         eax, 4
          INT         0x80
endif:
          MOV         ebx, 0
          MOV         eax, 1
          INT         0x80