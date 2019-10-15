@echo off
for %%a in (%*) do (
	echo ---------		One by one execute command: %%a
	%%a
)
