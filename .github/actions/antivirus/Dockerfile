FROM clamav/clamav:1.4.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
