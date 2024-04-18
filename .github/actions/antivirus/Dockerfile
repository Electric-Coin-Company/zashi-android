FROM clamav/clamav:1.3.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
