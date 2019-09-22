# FileUtility
Use for search duplicate files on your PC.  

    usage: FileUtility
    -d,--duplicateSort     Sort output by maximum files duplicate. By default
                           sort by maximum file size
    -e,--endWith <arg>     File extension to filter output. Example: pdf
    -h,--help              Show this help.
    -n,--number <arg>      Number of files to output. Example: 10
    -p,--path <arg>        Required parameter. The path or paths to the
                           folder or disk drive where to look for duplicate
                           files. Use space as separator. If path contains
                           spaces surround it with double quotes. Example:
                           "C:\Program Files" D:\
    -s,--startWith <arg>   Start of file name to filter output. Example: temp

Written to gain experience with:
- java.nio
- java.concurrent
- Apache Commons CLI
- Apache Maven
- slf4j and log4j
- Project Lombok
- JUnit and AssertJ
