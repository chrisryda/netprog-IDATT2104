FROM gcc:9.3.0
RUN mkdir /compile
WORKDIR /compile
COPY main.cpp /compile/
RUN g++ main.cpp -o main
CMD [ "./main" ]