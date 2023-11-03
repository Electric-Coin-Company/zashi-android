FROM clamav/clamav:1.2.1
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
